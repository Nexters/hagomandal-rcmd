package com.hagomandal.rcmd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hagomandal.rcmd.TestUtils;
import com.hagomandal.rcmd.component.MorphemeAnalyser;
import com.hagomandal.rcmd.model.input.Mandalart;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeywordGraphServiceImplTest {


    @Test
    void test_KeywordCollector() throws IOException, URISyntaxException {
        Mandalart sample = TestUtils.deserializeTo("sample/sample_mandalart_1.json", new TypeReference<List<Mandalart>>() {}).get(0);
        Set<String> destKeywords = sample.getGoal().getChildren().stream()
            .collect(KeywordGraphServiceImpl.KeywordCollector.toKeywordSet(new MorphemeAnalyser()));
        Assertions.assertTrue(TestUtils.compareSet(Set.of("이력서", "업데이트", "JD", "탐색", "CS", "기본기", "점검", "면접", "준비"), destKeywords));
    }

}
