package br.com.mvsouza.excel.bean;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClockIn {

    private String weekDay;
    private Integer monthDay;
    private String morningIn;
    private String morningOut;
    private String afternoonIn;
    private String afternoonOut;
    private String extraIn;
    private String extraOut;
    private boolean sunday;

}
