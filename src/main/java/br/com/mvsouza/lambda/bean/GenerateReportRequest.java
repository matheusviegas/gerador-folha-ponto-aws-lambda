package br.com.mvsouza.lambda.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateReportRequest {

    private String name;
    private String email;
    private String password;
    private String dateReference;
    private String afternoonShiftStart;

}
