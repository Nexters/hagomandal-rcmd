package com.hagomandal.rcmd.model.keyword;

import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@Data
@RequiredArgsConstructor(staticName = "of")
@RelationshipProperties
public class FlowRelationship {

    @RelationshipId
    private Long id;

    private final String jobType;
    private final int goalLevel;

    private float weight = 1;

    @TargetNode
    private final KeywordEntity keyword;

    @Override
    public boolean equals(Object o) {
        if (o instanceof FlowRelationship) {
            FlowRelationship target = (FlowRelationship) o;
            return jobType.equals(target.getJobType()) && goalLevel == target.getGoalLevel() && keyword.getKeyword().equals(target.getKeyword());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobType, goalLevel);
    }
}
