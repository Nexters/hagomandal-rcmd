package com.hagomandal.rcmd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hagomandal.rcmd.TestUtils;
import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KeywordGraphServiceImplSpringBootTest {

    @Autowired
    private KeywordGraphServiceImpl target;

    @Test
    void test_updatePartial() throws IOException, URISyntaxException, InterruptedException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        target.updatePartial(sample.getGoal(), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(0), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(1), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(2), sample.getInfo()).block();
        target.updatePartial(sample.getGoal().getChildren().get(3), sample.getInfo()).block();
    }

    @Test
    void test_update() throws IOException, URISyntaxException, InterruptedException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        target.update(sample).block();
        Thread.sleep(5 * 1000L);
    }

    @Test
    void test_retrieveSearchKeywords() {
        List<SearchKeyword> searchKeywordList = target.retrieveSearchKeywords(
            List.of("이직"),
            new Info("software_developer", "backend_engineer", List.of()),
            1
        ).block();
        System.out.println(searchKeywordList);

        List<SearchKeyword> searchKeywordList2 = target.retrieveSearchKeywords(
            List.of("이력서", "업데이트"),
            new Info("software_developer", "backend_engineer", List.of()),
            2
        ).block();
        System.out.println(searchKeywordList2);
    }
}
