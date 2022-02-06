package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.graph.keyword.KeywordEntity;
import java.util.Set;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface KeywordRepository extends ReactiveNeo4jRepository<KeywordEntity, String> {

    Mono<KeywordEntity> findOneByKeyword(String keyword);

    String FIND_OR_CREATE_WITH_KEYWORDS_QUERY = ""
        + "UNWIND $keywords AS kw\n"
        + "MERGE (k: Keyword {keyword: kw})\n"
        + "ON CREATE\n"
        + " SET k.frequency = 0\n"
        + "ON MATCH\n"
        + " SET k.frequency = k.frequency + 1\n"
        + "RETURN k";

    @Query(FIND_OR_CREATE_WITH_KEYWORDS_QUERY)
    Flux<KeywordEntity> findOrCreateWithKeywords(@Param("keywords") Set<String> keywords);

    String FIND_SEARCH_KEYWORDS_QUERY = ""
        + "MATCH (s: Keyword {keyword: $inputKeyword})-[r: FLOW {jobType: $jobType, goalLevel: $targetLevel}]->(t: Keyword)\n"
        + "RETURN t, collect(r), collect(s)";

    @Query(FIND_SEARCH_KEYWORDS_QUERY)
    Flux<KeywordEntity> findSearchKeywords(
        @Param("inputKeyword") String inputKeyword, @Param("jobType") String jobType, @Param("targetLevel") int targetLevel);
}
