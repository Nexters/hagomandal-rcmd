package com.hagomandal.rcmd.component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.stereotype.Component;

@Component
public class MorphemeAnalyser {

    // NOTE: Tag 종류와 관련해서는 Komoran 품사표 참고: https://docs.komoran.kr/firststep/postypes.html
    private static List<String> VALID_TAGS = List.of(
        "NNG",  // 일반명사
        "NNP",
        "NP",
        "VV",
        "VA",
        "SL"
    );

    private Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

    public Set<String> extractKeywords(String phrase) {
        KomoranResult result = komoran.analyze(phrase);

        return result.getList().stream()
            .filter(pair -> VALID_TAGS.contains(pair.getSecond()))
            .map(pair -> pair.getFirst())
            .collect(Collectors.toSet());
    }
}
