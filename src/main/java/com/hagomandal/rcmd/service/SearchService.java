package com.hagomandal.rcmd.service;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.hagomandal.rcmd.model.GoalDocument;
import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.input.Info;
import com.hagomandal.rcmd.model.input.Mandalart;
import java.util.List;
import reactor.core.publisher.Mono;

public interface SearchService {

    Mono<BulkResponse> index(Mandalart mandalart);
    Mono<SearchResponse<GoalDocument>> search(List<SearchKeyword> searchKeywordList, int goalLevel, Info info);
}
