package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import com.hagomandal.rcmd.model.mandalart.GoalDetail;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import java.util.List;
import reactor.core.publisher.Mono;

public interface KeywordGraphService {

    Mono<List<KeywordEntity>> updatePartial(GoalDetail goalDetail, Info info);
    Mono<List<KeywordEntity>> update(Mandalart mandalart);
    Mono<List<SearchKeyword>> retrieveSearchKeywords(List<String> keywords, Info info, int targetLevel);
}
