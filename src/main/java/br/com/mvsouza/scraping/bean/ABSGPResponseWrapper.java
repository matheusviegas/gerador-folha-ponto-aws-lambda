package br.com.mvsouza.scraping.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ABSGPResponseWrapper {

    private List<ABSGPClockInItem> timesheets;

}
