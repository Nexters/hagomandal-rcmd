package com.hagomandal.rcmd.service;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hagomandal.rcmd.TestUtils;
import com.hagomandal.rcmd.model.GoalDocument;
import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchServiceImplSpringBootTest {

    @Autowired
    private SearchServiceImpl target;

    @Test
    void test_index() throws IOException, URISyntaxException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        BulkResponse bulkResponse = target.index(sample).block();
        System.out.println(bulkResponse);
    }

    @Test
    void test_search() {
        SearchResponse<GoalDocument> searchResponse = target.search(List.of(
            SearchKeyword.of("스타트업", 4.0f),
            SearchKeyword.of("지원", 2.0f)
        ), 2, "software_developer").block();
        System.out.println(searchResponse.hits().hits().stream().map(hit -> hit.source()).collect(Collectors.toList()));
    }
}
