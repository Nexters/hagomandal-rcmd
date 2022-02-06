package com.hagomandal.rcmd.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Mono;

@SpringBootTest
public class SynonymGraphServiceImplSpringBootTest {


    @Autowired
    private SynonymGraphServiceImpl target;

    @Test
    void test_resetGraphWithDict() throws IOException, InterruptedException {
        final ClassPathResource dictResource = new ClassPathResource("dict/synonym_test.dict");
        final Path dictPath = Paths.get(dictResource.getURI());
        target.resetGraphWithDict(dictPath);
        Thread.sleep(3000);
    }

    @Test
    void test_resetGraph() throws InterruptedException {
        target.resetGraph();
        Thread.sleep(3000);
    }

    @Test
    void test_extractKeywords() {
        Mono<Set<String>> keywordsMono = target.extractKeywords("소프트웨어 서적 읽기");
        Set<String> keywords = keywordsMono.block();
        System.out.println(keywords);

        Mono<Set<String>> keywordsMono2 = target.extractKeywords("프로그래머는 개발 도서를 가까이 해야한다.");
        Set<String> keywords2 = keywordsMono2.block();
        System.out.println(keywords2);
    }
}
