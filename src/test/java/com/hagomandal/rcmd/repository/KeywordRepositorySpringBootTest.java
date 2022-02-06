package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KeywordRepositorySpringBootTest {

    @Autowired
    private KeywordRepository target;

    @Test
    void test_findOneByKeyword() {
        KeywordEntity keywordEntity = target.findOneByKeyword("테스트").block();
        System.out.println(keywordEntity.getKeyword());
    }

    @Test
    void test_findByKeywords() {
        List<KeywordEntity> keywordEntityList = target.findOrCreateWithKeywords(Set.of("테스트", "고라니", "임시")).collectList().block();
        System.out.println(keywordEntityList.stream().map(KeywordEntity::getKeyword).collect(Collectors.toList()));
    }

    @Test
    void test_findSearchKeywords() {
        List<KeywordEntity> keywordEntityList = target.findSearchKeywords("탐색", "software_developer", 2).collectList().block();
        System.out.println(keywordEntityList.stream().map(KeywordEntity::getKeyword).collect(Collectors.toList()));
    }
}
