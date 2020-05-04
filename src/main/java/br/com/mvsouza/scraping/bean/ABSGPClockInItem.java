package br.com.mvsouza.scraping.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ABSGPClockInItem {

    private String date;
    @JsonProperty("initial_time")
    private String initialTime;
    @JsonProperty("final_time")
    private String finalTime;

}
