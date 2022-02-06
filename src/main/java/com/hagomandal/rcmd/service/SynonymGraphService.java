package com.hagomandal.rcmd.service;

import java.util.Set;
import reactor.core.publisher.Mono;

public interface SynonymGraphService {

    String resetGraph();
    Mono<Set<String>> extractKeywords(String phrase);
}
