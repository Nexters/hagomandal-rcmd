package com.hagomandal.rcmd.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Info {

    @JsonProperty("job_type_0")
    private String jobType0;
    @JsonProperty("job_type_1")
    private String jobType1;
    private List<String> interests;
}
