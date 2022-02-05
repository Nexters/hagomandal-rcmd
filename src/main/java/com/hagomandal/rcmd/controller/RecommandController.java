package com.hagomandal.rcmd.controller;

import com.hagomandal.rcmd.service.KeywordGraphService;
import com.hagomandal.rcmd.service.SynonymGraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin
@RequestMapping(value = "v0/rcmd")
@RestController
@RequiredArgsConstructor
public class RecommandController {

    private final SynonymGraphService synonymGraphService;
    private final KeywordGraphService keywordGraphService;

    @PostMapping(value = "synonym/reset")
    @ResponseBody
    public Mono<ResponseEntity<String>> reset() {
        return Mono.just(synonymGraphService.resetGraph())
            .map(result -> ResponseEntity.ok().body(result));
    }
}
