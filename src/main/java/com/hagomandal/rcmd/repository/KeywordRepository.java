package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.keyword.KeywordEntity;
import java.util.Set;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface KeywordRepository extends ReactiveNeo4jRepository<KeywordEntity, String> {

    String FIND_OR_CREATE_WITH_KEYWORDS_QUERY = ""
        + "UNWIND $keywords AS kw\n"
        + "MERGE (k:Keyword {keyword: kw})\n"
        + "ON CREATE\n"
        + " SET k.frequency = 0\n"
        + "ON MATCH\n"
        + " SET k.frequency = k.frequency + 1\n"
        + "RETURN k";

    Mono<KeywordEntity> findOneByKeyword(String keyword);

    @Query(FIND_OR_CREATE_WITH_KEYWORDS_QUERY)
    Flux<KeywordEntity> findOrCreateWithKeywords(@Param("keywords") Set<String> keywords);
}
