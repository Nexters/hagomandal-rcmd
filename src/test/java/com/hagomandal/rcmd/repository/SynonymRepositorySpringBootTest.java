package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.graph.synonym.WordEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
public class SynonymRepositorySpringBootTest {

    @Autowired
    private SynonymRepository target;

    @Test
    void test_findRepSynonym() throws InterruptedException {
        String targetWord = "developer";
        Mono<WordEntity> repMono = target.findRepSynonym(targetWord);
        String repWord = repMono.map(WordEntity::getWord).block();
        System.out.println(repWord);

        String targetWord2 = "개발자";
        Mono<WordEntity> repMono2 = target.findRepSynonym(targetWord2);
        String repWord2 = repMono2.map(WordEntity::getWord).block();
        System.out.println(repWord2);

        String targetWord3 = "오함마";
        Mono<WordEntity> repMono3 = target.findRepSynonym(targetWord3);
        String repWord3 = repMono3.map(WordEntity::getWord).block();
        System.out.println(repWord3);
    }
}
