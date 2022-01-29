package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.keyword.KeywordEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface KeywordRepository extends ReactiveNeo4jRepository<KeywordEntity, String> {

    Mono<KeywordEntity> findOneByKeyword(String keyword);

    @Query("MATCH (k: Keyword) DETACH DELETE k")
    Mono<Void> deleteAllKeywords();
}
