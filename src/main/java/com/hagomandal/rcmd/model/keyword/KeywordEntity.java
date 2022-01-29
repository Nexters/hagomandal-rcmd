package com.hagomandal.rcmd.model.keyword;

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
@Node("Keyword")
public class KeywordEntity {

    @Id
    private final String keyword;

    @Property
    private final boolean representative;

    @Relationship(type = "FOLLOW", direction = Direction.OUTGOING)
    private final List<SynonymRelationship> followings;
}
