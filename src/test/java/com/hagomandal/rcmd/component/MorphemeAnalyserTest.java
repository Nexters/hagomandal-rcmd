package com.hagomandal.rcmd.component;

import static com.hagomandal.rcmd.TestUtils.compareSet;

import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MorphemeAnalyserTest {

    private MorphemeAnalyser morphemeAnalyser = new MorphemeAnalyser();

    @Test
    void test_extractKeywords() {
        String phrase = "개인 프로젝트 진행 내용 업데이트";
        Set<String> keywords = morphemeAnalyser.extractKeywords(phrase);

        Assertions.assertEquals(5, keywords.size());
        Assertions.assertTrue(compareSet(Set.of("개인", "프로젝트", "진행", "내용", "업데이트"), keywords));
    }
}
