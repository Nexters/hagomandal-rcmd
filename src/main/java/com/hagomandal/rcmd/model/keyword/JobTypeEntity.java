package com.hagomandal.rcmd.model.keyword;

import com.hagomandal.rcmd.model.synonym.SynonymRelationship;
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
@Node("JobType")
public class JobTypeEntity {

    @Id
    private final String jobTypeId;

    @Property
    private final int jobTypeLevel;

    @Property
    private final String jobType;

    @Relationship(type = "FOLLOW", direction = Direction.OUTGOING)
    private final List<SynonymRelationship> followings;
}
