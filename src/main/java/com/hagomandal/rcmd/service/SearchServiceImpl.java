package com.hagomandal.rcmd.service;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.hagomandal.rcmd.config.ElasticsearchProperties;
import com.hagomandal.rcmd.model.GoalDocument;
import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.mandalart.GoalDetail;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private static final String INDEX_NAME = "mandalart";
    private static final int MAX_SEARCH_KEYWORD_COUNT = 10;
    public static final String MINIMUM_SHOULD_MATCH = "1";

    private final ElasticsearchProperties esProperties;
    private final RestClient restClient;
    private final ElasticsearchTransport transport;
    private final ElasticsearchAsyncClient client;

    public SearchServiceImpl(
        ElasticsearchProperties elasticsearchProperties
    ) {
        esProperties = elasticsearchProperties;
        restClient = RestClient.builder(new HttpHost(this.esProperties.getHost(), this.esProperties.getPort())).build();
        // NOTE: JacksonJsonpMapper class not found issue: https://github.com/elastic/elasticsearch-java/issues/79
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchAsyncClient(transport);
    }

    @Override
    public Mono<BulkResponse> index(Mandalart mandalart) {

        Info info = mandalart.getInfo();

        GoalDetail mainGoal = mandalart.getGoal();
        List<GoalDetail> midGoals = mandalart.getGoal().getChildren();
        List<GoalDetail> leafGoals = midGoals.stream()
            .map(_midGoal -> _midGoal.getChildren())
            .flatMap(List::stream)
            .collect(Collectors.toList());

        List<GoalDocument> goalDocumentList = new ArrayList<>();
        goalDocumentList.add(buildGoalDocument(mainGoal, 0, info));
        goalDocumentList.addAll(midGoals.stream().map(_midGoal -> buildGoalDocument(_midGoal, 1, info)).collect(Collectors.toList()));
        goalDocumentList.addAll(leafGoals.stream().map(_leafGoal -> buildGoalDocument(_leafGoal, 2, info)).collect(Collectors.toList()));

        BulkRequest bulkRequest = BulkRequest.of(_builder -> _builder.index(INDEX_NAME).operations(createBulkIndexOperations(goalDocumentList)));

        try {
            return Mono.fromFuture(client.bulk(bulkRequest));
        } catch (IOException e) {
            return Mono.empty();
        }
    }

    @Override
    public Mono<SearchResponse<GoalDocument>> search(List<SearchKeyword> searchKeywordList, int goalLevel, String jobType0) {
        List<SearchKeyword> queryKeywordList;
        if (searchKeywordList.size() > MAX_SEARCH_KEYWORD_COUNT) {
            queryKeywordList = searchKeywordList.subList(0, MAX_SEARCH_KEYWORD_COUNT);
        } else {
            queryKeywordList = searchKeywordList;
        }

        BoolQuery.Builder rootQueryBuilder = QueryBuilders.bool();

        List<Query> matchQueryList = queryKeywordList.stream()
            .map(_queryKeyword -> Query.of(
                _builder -> _builder.match(buildMatchQuery("goal", _queryKeyword.getKeyword(), _queryKeyword.getWeight())))
            )
            .collect(Collectors.toList());
        rootQueryBuilder.should(matchQueryList).minimumShouldMatch(MINIMUM_SHOULD_MATCH);

        Query jobType0Query = Query.of(_builder -> _builder.term(buildTermQuery("jobType0", jobType0)));
        Query goalLevelQuery = Query.of(_builder -> _builder.term(buildTermQuery("level", goalLevel)));
        rootQueryBuilder.filter(jobType0Query, goalLevelQuery);

        Query query = Query.of(_builder -> _builder.bool(rootQueryBuilder.build()));
        SearchRequest searchRequest = SearchRequest.of(_builder -> _builder.index(INDEX_NAME).query(query).from(0).size(10));
        try {
            return Mono.fromFuture(client.search(searchRequest, GoalDocument.class));
        } catch (IOException e) {
            return Mono.empty();
        }
    }

    static MatchQuery buildMatchQuery(String fieldName, String value, float boost) {
        return MatchQuery.of(_builder -> _builder.field(fieldName).query(FieldValue.of(value)).boost(boost));
    }

    static TermQuery buildTermQuery(String fieldName, String value) {
        return TermQuery.of(_builder -> _builder.field(fieldName).value(FieldValue.of(value)));
    }

    static TermQuery buildTermQuery(String fieldName, int value) {
        return TermQuery.of(_builder -> _builder.field(fieldName).value(FieldValue.of(value)));
    }


    private GoalDocument buildGoalDocument(GoalDetail goalDetail, int level, Info info) {
        String jobType0 = info.getJobType0();
        String jobType1 = info.getJobType1();
        return GoalDocument.builder().goal(goalDetail.getDesc()).level(level).jobType0(jobType0).jobType1(jobType1).build();
    }

    private List<BulkOperation> createBulkIndexOperations(List<GoalDocument> goalDocumentList) {
        return goalDocumentList.stream()
            .map(_goalDocument -> BulkOperation.of(
                _boBuilder -> _boBuilder.index(IndexOperation.of(
                    _ioBuilder -> _ioBuilder.index(INDEX_NAME).document(_goalDocument))))
            )
            .collect(Collectors.toList());
    }
}
