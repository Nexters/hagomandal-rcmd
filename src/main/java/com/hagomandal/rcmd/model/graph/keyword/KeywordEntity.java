package com.hagomandal.rcmd.model.graph.keyword;

import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

@Data
@RequiredArgsConstructor(staticName = "of")
@Node("Keyword")
public class KeywordEntity {

    @Id
    private final String keyword;

    @Property
    private int frequency;

    @Relationship(type = "FLOW", direction = Direction.OUTGOING)
    private List<FlowRelationship> outFlows;

    @Relationship(type = "FLOW", direction = Direction.INCOMING)
    private List<FlowRelationship> inFlows;

    @Override
    public boolean equals(Object o) {
        if (o instanceof KeywordEntity) {
            KeywordEntity target = (KeywordEntity) o;
            return keyword.equals(target.getKeyword());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword);
    }
}
