package com.hagomandal.rcmd;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.junit.jupiter.api.Test;

public class KomoranTest {

    private Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    @Test
    void test_komoran() {
        String strToAnalyze = "대한민국은 민주공화국이다. Republic of Korea is a democratic republic.";
        KomoranResult analyzeResultList = komoran.analyze(strToAnalyze);
        System.out.println(analyzeResultList.getList());

        String strToAnalyze2 = "그 외 CS 필수 지식 리스트업 및 점검";
        System.out.println(komoran.analyze(strToAnalyze2).getList());
    }
}
