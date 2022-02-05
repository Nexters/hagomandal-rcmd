package com.hagomandal.rcmd.service;

import com.hagomandal.rcmd.component.MorphemeAnalyser;
import com.hagomandal.rcmd.model.input.GoalDetail;
import com.hagomandal.rcmd.model.input.Info;
import com.hagomandal.rcmd.model.input.Mandalart;
import com.hagomandal.rcmd.model.keyword.FlowRelationship;
import com.hagomandal.rcmd.model.keyword.KeywordEntity;
import com.hagomandal.rcmd.repository.KeywordRepository;
import java.util.ArrayList;
import java.util.Collections;
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
                    _srcKeywordEntity.getFlows().stream().forEach(_flow -> {
                        if (cpFlowList.contains(_flow)) {
                            _flow.setWeight(_flow.getWeight() + 1);
                            cpFlowList.remove(_flow);
                        }
                    });
                    _srcKeywordEntity.getFlows().addAll(cpFlowList);
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
            _midGoal.getChildren().forEach(_leafGoal -> {
                allKeywords.addAll(morphemeAnalyser.extractKeywords(_leafGoal.getDesc()));
            });
        });

        // TODO: 비동기로 해결 필요
        List<KeywordEntity> keywordEntityList = keywordRepository.findOrCreateWithKeywords(allKeywords).collectList().block();
        updatePartial(mandalart.getGoal(), mandalart.getInfo()).block();
        updatePartial(mandalart.getGoal().getChildren().get(0), mandalart.getInfo()).block();
        updatePartial(mandalart.getGoal().getChildren().get(1), mandalart.getInfo()).block();
        updatePartial(mandalart.getGoal().getChildren().get(2), mandalart.getInfo()).block();
        updatePartial(mandalart.getGoal().getChildren().get(3), mandalart.getInfo()).block();

        return Mono.just(keywordEntityList);
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
