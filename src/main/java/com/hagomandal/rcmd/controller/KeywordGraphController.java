package com.hagomandal.rcmd.controller;

import com.hagomandal.rcmd.service.KeywordGraphService;
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
@RequestMapping(value = "v0/keyword")
@RestController
@RequiredArgsConstructor
public class KeywordGraphController {

    private final KeywordGraphService keywordGraphService;

    @PostMapping(value = "reset")
    @ResponseBody
    public Mono<ResponseEntity<String>> reset() {
        return Mono.just(keywordGraphService.resetGraph())
            .map(result -> ResponseEntity.ok().body(result));
    }
}
