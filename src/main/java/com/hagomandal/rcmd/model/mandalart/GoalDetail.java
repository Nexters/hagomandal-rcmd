package com.hagomandal.rcmd.model.mandalart;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDetail {

    private String desc;
    @JsonProperty("goal_level")
    private int goalLevel;
    private List<GoalDetail> children;
    private List<String> actions;
}
