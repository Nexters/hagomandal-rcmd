package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.component.MorphemeAnalyser;
import com.hagomandal.rcmd.model.SearchKeyword;
import com.hagomandal.rcmd.model.mandalart.GoalDetail;
import com.hagomandal.rcmd.model.mandalart.Info;
import com.hagomandal.rcmd.model.mandalart.Mandalart;
import com.hagomandal.rcmd.model.graph.keyword.FlowRelationship;
import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import com.hagomandal.rcmd.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordGraphServiceImpl implements KeywordGraphService {

    private static final float FREQUENCY_WEIGHT = 0.5f;
    private static final float RELATION_WEIGHT = 2.0f;

    private final KeywordRepository keywordRepository;
    private final MorphemeAnalyser morphemeAnalyser;

    @Override
    public Mono<List<KeywordEntity>> updatePartial(GoalDetail goalDetail, Info info) {
        int goalLevel = goalDetail.getGoalLevel() + 1;

        Set<String> srcKeywordList = morphemeAnalyser.extractKeywords(goalDetail.getDesc());
        Set<String> destKeywordList = goalDetail.getChildren().stream().collect(KeywordCollector.toKeywordSet(morphemeAnalyser)).stream()
            .filter(_keyword -> !srcKeywordList.contains(_keyword))
            .collect(Collectors.toSet());

        Flux<KeywordEntity> srcKeywordEntityFlux = keywordRepository.findOrCreateWithKeywords(srcKeywordList);
        Flux<KeywordEntity> destKeywordEntityFlux = keywordRepository.findOrCreateWithKeywords(destKeywordList);

        return Mono.zip(srcKeywordEntityFlux.collectList(), destKeywordEntityFlux.collectList())
            .map(tuple -> {
                Set<KeywordEntity> keywordEntitySet = new HashSet<>();
                Set<KeywordEntity> srcKeywordEntityList = tuple.getT1().stream().collect(Collectors.toSet());
                Set<KeywordEntity> destKeywordEntityList = tuple.getT2().stream()
                    .filter(_keywordEntity -> !srcKeywordEntityList.contains(_keywordEntity))
                    .collect(Collectors.toSet());

                List<FlowRelationship> flowList = destKeywordEntityList.stream()
                    .map(_destKeywordEntity -> List.of(
                        FlowRelationship.of(info.getJobType0(), goalLevel, _destKeywordEntity),
                        FlowRelationship.of(info.getJobType1(), goalLevel, _destKeywordEntity)
                    ))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

                srcKeywordEntityList.stream().forEach(_srcKeywordEntity -> {
                    List<FlowRelationship> cpFlowList = new ArrayList<>();
                    cpFlowList.addAll(flowList);
                    _srcKeywordEntity.getOutFlows().stream().forEach(_flow -> {
                        if (cpFlowList.contains(_flow)) {
                            _flow.setWeight(_flow.getWeight() + 1);
                            cpFlowList.remove(_flow);
                        }
                    });
                    _srcKeywordEntity.getOutFlows().addAll(cpFlowList);
                });

                keywordEntitySet.addAll(srcKeywordEntityList);
                keywordEntitySet.addAll(destKeywordEntityList);
                return keywordEntitySet;
            })
            .flatMapMany(keywordEntitySet -> keywordRepository.saveAll(keywordEntitySet))
            .collectList();
    }

    @Override
    public Mono<List<KeywordEntity>> update(Mandalart mandalart) {

        Set<String> allKeywords = new HashSet<>();
        allKeywords.addAll(morphemeAnalyser.extractKeywords(mandalart.getGoal().getDesc()));
        mandalart.getGoal().getChildren().stream().forEach(_midGoal -> {
            allKeywords.addAll(morphemeAnalyser.extractKeywords(_midGoal.getDesc()));
            _midGoal.getChildren().forEach(_leafGoal -> allKeywords.addAll(morphemeAnalyser.extractKeywords(_leafGoal.getDesc())));
        });

        return keywordRepository.findOrCreateWithKeywords(allKeywords).collectList()
            .doOnSuccess(_keywordEntityList -> updatePartial(mandalart.getGoal(), mandalart.getInfo())
                    .flatMap(_r -> updatePartial(mandalart.getGoal().getChildren().get(0), mandalart.getInfo()))
                    .flatMap(_r -> updatePartial(mandalart.getGoal().getChildren().get(1), mandalart.getInfo()))
                    .flatMap(_r -> updatePartial(mandalart.getGoal().getChildren().get(2), mandalart.getInfo()))
                    .flatMap(_r -> updatePartial(mandalart.getGoal().getChildren().get(3), mandalart.getInfo()))
                    .subscribe()
            );
    }

    @Override
    public Mono<List<SearchKeyword>> retrieveSearchKeywords(List<String> keywords, Info info, int targetLevel) {
        return Flux.fromIterable(keywords)
            .parallel()
            .flatMap(_kw -> keywordRepository.findSearchKeywords(_kw, info.getJobType0(), targetLevel))
            .sequential()
            .map(_ke -> SearchKeyword.of(_ke.getKeyword(), getKeywordFrequencyFactor(_ke)))
            .collectList()
            .map(_searchKeywordList -> new ArrayList<>(new HashSet<>(_searchKeywordList)))   // 중복 제거
            .map(_searchKeywordList -> {
                _searchKeywordList.sort(Comparator.comparingDouble(SearchKeyword::getWeight).reversed());
                return _searchKeywordList;
            });
    }

    private float getKeywordFrequencyFactor(KeywordEntity keywordEntity) {
        return FREQUENCY_WEIGHT * keywordEntity.getFrequency()
            + RELATION_WEIGHT * keywordEntity.getInFlows().stream().map(flow -> flow.getWeight()).reduce((w1, w2) -> w1 + w2).orElse(0.0f);
    }

    @RequiredArgsConstructor
    static class KeywordCollector implements Collector<GoalDetail, Set<String>, Set<String>> {

        private final MorphemeAnalyser morphemeAnalyser;

        public static KeywordCollector toKeywordSet(MorphemeAnalyser morphemeAnalyser) {
            return new KeywordCollector(morphemeAnalyser);
        }

        @Override
        public Supplier<Set<String>> supplier() {
            return HashSet::new;
        }

        @Override
        public BiConsumer<Set<String>, GoalDetail> accumulator() {
            return (set, goal) -> set.addAll(morphemeAnalyser.extractKeywords(goal.getDesc()));
        }

        @Override
        public BinaryOperator<Set<String>> combiner() {
            return (set1, set2) -> {
                set1.addAll(set2);
                return set1;
            };
        }

        @Override
        public Function<Set<String>, Set<String>> finisher() {
            return Collections::unmodifiableSet;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of(Characteristics.UNORDERED);
        }
    }
}
