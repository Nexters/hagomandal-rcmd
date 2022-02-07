package com.hagomandal.rcmd.controller;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.hagomandal.rcmd.model.GoalDocument;
import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import com.hagomandal.rcmd.model.recommend.RcmdRequestBody;
import com.hagomandal.rcmd.service.KeywordGraphService;
import com.hagomandal.rcmd.service.SearchService;
import com.hagomandal.rcmd.service.SynonymGraphService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@CrossOrigin
@RequestMapping(value = "v0")
@RestController
@RequiredArgsConstructor
public class RecommandController {

    private final SynonymGraphService synonymGraphService;
    private final KeywordGraphService keywordGraphService;
    private final SearchService searchService;

    @PostMapping(value = "synonym")
    @ResponseBody
    public Mono<ResponseEntity<String>> reset() {
        return Mono.just(synonymGraphService.resetGraph())
            .map(result -> ResponseEntity.ok().body(result));
    }

    @PostMapping(value = "mandalart")
    @ResponseBody
    public Mono<ResponseEntity<String>> registerMandalart(@RequestBody Mandalart mandalart) {

        Mono<List<KeywordEntity>> keywordGraphUpdateMono = keywordGraphService.update(mandalart);
        Mono<BulkResponse> indexMono = searchService.index(mandalart);

        // error 케이스 처리 필요
        return Mono.zip(keywordGraphUpdateMono, indexMono)
            .map(_tuple -> "ok")
            .map(ResponseEntity::ok);
    }

    @GetMapping(value = "rcmd")
    @ResponseBody
    public Mono<ResponseEntity<List<GoalDocument>>> recommend(@RequestBody RcmdRequestBody requestBody) {
        Info info = requestBody.getInfo();
        int targetGoalLevel = requestBody.getGoal().getGoalLevel() + 1;

        return synonymGraphService.extractKeywords(requestBody.getGoal().getDesc())
            .flatMap(_keywordSet -> keywordGraphService.retrieveSearchKeywords(new ArrayList<>(_keywordSet), info, targetGoalLevel))
            .flatMap(_searchKeywordList -> searchService.search(_searchKeywordList, targetGoalLevel, info.getJobType0()))
            .map(_searchResponse -> _searchResponse.hits().hits().stream().map(_hit -> _hit.source()).collect(Collectors.toList()))
            .map(ResponseEntity::ok);
    }
}
