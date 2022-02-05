package com.hagomandal.rcmd.model.input;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalDetail {

    private String desc;
    private int goalLevel;
    private List<GoalDetail> children;
    private List<String> actions;
}
