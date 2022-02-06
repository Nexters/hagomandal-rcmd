package com.hagomandal.rcmd.service;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.hagomandal.rcmd.model.input.Mandalart;
import reactor.core.publisher.Mono;

public interface SearchService {

    Mono<BulkResponse> index(Mandalart mandalart);
}
