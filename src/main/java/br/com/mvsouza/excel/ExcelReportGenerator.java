package br.com.mvsouza.excel;

import br.com.mvsouza.excel.bean.ClockIn;
import br.com.mvsouza.helpers.DateHelper;
import br.com.mvsouza.helpers.ExcelHelper;
import br.com.mvsouza.helpers.GeneralHelpers;
import br.com.mvsouza.scraping.bean.ABSGPClockInItem;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelReportGenerator {

    private ABSGPResponseWrapper absgpData;
    private ScrapingInfo scrapingInfo;

    public byte[] generateReport() throws IOException {
        return ExcelHelper.builder().scrapingInfo(scrapingInfo).clockIns(parseABSGPResponse()).build().generateContent();
    }

    private List<ClockIn> parseABSGPResponse() {
        Map<String, List<String>> timesGroupedByDate = new HashMap<>();

        for (ABSGPClockInItem absgpClockInItem : absgpData.getTimesheets()) {
            timesGroupedByDate.putIfAbsent(absgpClockInItem.getDate(), new ArrayList<>());
            timesGroupedByDate.get(absgpClockInItem.getDate()).add(absgpClockInItem.getInitialTime());
            timesGroupedByDate.get(absgpClockInItem.getDate()).add(absgpClockInItem.getFinalTime());
        }

        Map<String, ClockIn> blackRegistersMap = generateBlankRegistersByMonthReference();

        for (Map.Entry<String, List<String>> item : timesGroupedByDate.entrySet()) {
            ClockIn clockIn = blackRegistersMap.get(item.getKey());

            int morningOutIndex = findMorningFinishIndex(item.getValue());

            clockIn.setMorningIn(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), 0, "")));
            clockIn.setMorningOut(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), morningOutIndex, "")));
            clockIn.setAfternoonIn(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), morningOutIndex + 1, "")));
            clockIn.setAfternoonOut(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), item.getValue().size() - 1, "")));
            clockIn.setExtraIn(GeneralHelpers.formatTime(""));
            clockIn.setExtraOut(GeneralHelpers.formatTime(""));
        }

        List<ClockIn> clockInList = new ArrayList<>(blackRegistersMap.values());

        Collections.sort(clockInList, Comparator.comparing(ClockIn::getMonthDay));

        return clockInList;
    }


    private Map<String, ClockIn> generateBlankRegistersByMonthReference() {
        Map<String, ClockIn> clockinRegisterMap = new HashMap<>();
        Calendar calendar = scrapingInfo.getDateReference();

        for (int dia = 1; dia <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); dia++) {
            calendar.set(Calendar.DAY_OF_MONTH, dia);

            ClockIn clockInRegister = ClockIn.builder()
                    .weekDay(DateHelper.getWeekDayName(calendar))
                    .monthDay(dia)
                    .sunday(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    .afternoonIn("")
                    .afternoonOut("")
                    .morningIn("")
                    .morningOut("")
                    .extraIn("")
                    .extraOut("")
                    .build();

            clockinRegisterMap.put(DateHelper.format(calendar.getTime(), DateHelper.DateFormat.YEAR_MONTH_DAY_DASH), clockInRegister);
        }

        return clockinRegisterMap;
    }

    private int findMorningFinishIndex(List<String> items) {
        LocalDateTime midday = LocalDateTime.now().withHour(11).withMinute(59).withSecond(0).withNano(0);
        LocalDateTime onePm = midday.plusHours(1);

        int index = 2;

        for (String hour : items) {
            String[] splittedHour = hour.split(":");
            LocalDateTime hourDate = LocalDateTime.now().withHour(Integer.parseInt(splittedHour[0])).withMinute(Integer.parseInt(splittedHour[1])).withSecond(0).withNano(0);

            if(!(hourDate.isAfter(midday) && hourDate.isBefore(onePm))) {
                continue;
            }

            index = items.indexOf(hour);

        }
        return index;

    }

}
