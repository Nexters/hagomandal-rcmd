package com.hagomandal.rcmd.model.recommend;

import com.hagomandal.rcmd.model.mandalart.GoalDetail;
import com.hagomandal.rcmd.model.mandalart.Info;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RcmdRequestBody {

    private GoalDetail goal;
    private Info info;
}
