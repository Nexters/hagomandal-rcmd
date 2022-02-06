package com.hagomandal.rcmd.repository;

import com.hagomandal.rcmd.model.graph.synonym.WordEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SynonymRepository extends ReactiveNeo4jRepository<WordEntity, String> {

    Mono<WordEntity> findOneByWord(String word);

    @Query("MATCH (w: Word) DETACH DELETE w")
    Mono<Void> deleteAllWords();

    String FIND_REP_SYNONYM_QUERY = ""
        + "MATCH (w: Word {word: $word})-[:FOLLOW]->(rep: Word)\n"
        + "RETURN rep";

    @Query(FIND_REP_SYNONYM_QUERY)
    Mono<WordEntity> findRepSynonym(String word);
}
