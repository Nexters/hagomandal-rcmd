package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.synonym.WordEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SynonymRepository extends ReactiveNeo4jRepository<WordEntity, String> {

    Mono<WordEntity> findOneByWord(String word);

    @Query("MATCH (k: Word) DETACH DELETE k")
    Mono<Void> deleteAllWords();
}
