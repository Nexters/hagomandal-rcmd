package com.hagomandal.rcmd.model.synonym;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RequiredArgsConstructor(staticName = "of")
@RelationshipProperties
public class SynonymRelationship {

    @RelationshipId
    private Long id;

    private final float weight;

    @TargetNode
    private final WordEntity word;

    public float getWeight() {
        return weight;
    }
}
