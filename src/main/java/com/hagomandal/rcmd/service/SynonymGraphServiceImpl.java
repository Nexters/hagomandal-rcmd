package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.component.MorphemeAnalyser;
import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import com.hagomandal.rcmd.model.graph.synonym.SynonymRelationship;
import com.hagomandal.rcmd.model.graph.synonym.WordEntity;
import com.hagomandal.rcmd.repository.SynonymRepository;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SynonymGraphServiceImpl implements SynonymGraphService {

    private static final String DICT_FILE_PATH = "dict/synonym.dict";
    private static final String REP_WORD_DELIMITER = ":";
    private static final String WORD_DELIMITER = ",";
    private boolean reconstructing = false;

    private final SynonymRepository synonymRepository;
    private final MorphemeAnalyser morphemeAnalyser;

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
            return "failed to load synonym dictionary: " + e;
        } catch (FileSystemNotFoundException e) {
            dictPath = Paths.get("dict", "synonym.dict");
        }

        resetGraphWithDict(dictPath);

        return "requested";
    }

    @Override
    public Mono<Set<String>> extractKeywords(String phrase) {
        Set<String> keywords = morphemeAnalyser.extractKeywords(phrase);
        Flux<WordEntity> repKeywordFlux = Flux.fromIterable(keywords)
            .flatMap(synonymRepository::findRepSynonym)
            .filter(Objects::nonNull);

        return repKeywordFlux
            .map(WordEntity::getWord)
            .collectList()
            .map(repKeywordList -> {
                keywords.addAll(repKeywordList);
                return keywords;
            });
    }

    public void resetGraphWithDict(Path dictPath) {
        Flux<String> linesFlux = Flux.using(
            () -> Files.lines(dictPath),
            Flux::fromStream,
            Stream::close
        );

        synonymRepository.deleteAllWords().doOnSuccess((Void) -> {
            reconstructing = true;
            linesFlux.doOnComplete(() -> {
                reconstructing = false;
            }).subscribe(this::processKeywordDictLine);
        }).subscribe();
    }

    void processKeywordDictLine(String line) {
        String[] a = line.split(REP_WORD_DELIMITER);
        if (a.length != 2) {
            log.warn("Failed to process keyword dict line: ", line);
            return;
        }
        WordEntity repWord = WordEntity.of(a[0], true, List.of());
        List<WordEntity> words = Arrays.stream(a[1].split(WORD_DELIMITER))
            .map(kw -> kw.trim())
            .map(kw -> WordEntity.of(kw, false, List.of(SynonymRelationship.of(1.0f, repWord))))
            .collect(Collectors.toList());
        words.add(repWord);
        synonymRepository.saveAll(words).subscribe();
    }
}
