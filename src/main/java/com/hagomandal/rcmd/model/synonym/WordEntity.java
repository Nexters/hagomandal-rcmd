package com.hagomandal.rcmd.model.synonym;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

@Getter
@RequiredArgsConstructor(staticName = "of")
@Node("Word")
public class WordEntity {

    @Id
    private final String word;

    @Property
    private final boolean representative;

    @Relationship(type = "FOLLOW", direction = Direction.OUTGOING)
    private final List<SynonymRelationship> followings;
}
