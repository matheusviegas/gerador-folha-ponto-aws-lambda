package br.com.mvsouza.scraping.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapingInfo {

    private String name;
    private String email;
    private String password;
    private Calendar dateReference;

}
