package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.model.keyword.KeywordEntity;
import com.hagomandal.rcmd.model.keyword.SynonymRelationship;
import com.hagomandal.rcmd.repository.KeywordRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordGraphServiceImpl implements KeywordGraphService {

    private static final String DICT_FILE_PATH = "dict/synonym.dict";
    private static final String REP_KEYWORD_DELIMITER = ":";
    private static final String KEYWORD_DELIMITER = ",";
    private boolean reconstructing = false;

    private final KeywordRepository keywordRepository;

    @Override
    public String resetGraph() {

        if (reconstructing) {
            return "on reconstruction";
        }

        Path dictPath;
        try {
            final ClassPathResource dictResource = new ClassPathResource(DICT_FILE_PATH);
            dictPath = Paths.get(dictResource.getURI());
        } catch (IOException e) {
            return "failed to load keyword dictionary: " + e;
        }

        resetGraphWithDict(dictPath);

        return "requested";
    }

    public void resetGraphWithDict(Path dictPath) {
        Flux<String> linesFlux = Flux.using(
            () -> Files.lines(dictPath),
            Flux::fromStream,
            Stream::close
        );

        keywordRepository.deleteAllKeywords().doOnSuccess((Void) -> {
            reconstructing = true;
            linesFlux.doOnComplete(() -> {
                reconstructing = false;
            }).subscribe(this::processKeywordDictLine);
        }).subscribe();
    }

    void processKeywordDictLine(String line) {
        String[] a = line.split(REP_KEYWORD_DELIMITER);
        if (a.length != 2) {
            log.warn("Failed to process keyword dict line: ", line);
            return;
        }
        KeywordEntity repKeyword = KeywordEntity.of(a[0], true, List.of());
        List<KeywordEntity> keywords = Arrays.stream(a[1].split(KEYWORD_DELIMITER))
            .map(kw -> kw.trim())
            .map(kw -> KeywordEntity.of(kw, false, List.of(SynonymRelationship.of(1.0f, repKeyword))))
            .collect(Collectors.toList());
        keywords.add(repKeyword);
        keywordRepository.saveAll(keywords).subscribe();
    }
}
