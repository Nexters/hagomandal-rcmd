package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.model.input.Info;
import com.hagomandal.rcmd.model.input.Mandalart;
import com.hagomandal.rcmd.model.input.GoalDetail;
import com.hagomandal.rcmd.model.keyword.KeywordEntity;
import java.util.List;
import reactor.core.publisher.Mono;

public interface KeywordGraphService {

    Mono<List<KeywordEntity>> updatePartial(GoalDetail goalDetail, Info info);
    Mono<List<KeywordEntity>> update(Mandalart mandalart);
}
