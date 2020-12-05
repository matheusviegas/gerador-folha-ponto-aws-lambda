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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    public List<ClockIn> parseABSGPResponse() {
        Map<String, List<String>> timesGroupedByDate = new HashMap<>();

        for (ABSGPClockInItem absgpClockInItem : absgpData.getTimesheets()) {
            timesGroupedByDate.putIfAbsent(absgpClockInItem.getDate(), new ArrayList<>());
            timesGroupedByDate.get(absgpClockInItem.getDate()).add(absgpClockInItem.getInitialTime());
            timesGroupedByDate.get(absgpClockInItem.getDate()).add(absgpClockInItem.getFinalTime());
        }

        Map<String, ClockIn> blackRegistersMap = generateBlankRegistersByMonthReference();

        for (Map.Entry<String, List<String>> item : timesGroupedByDate.entrySet()) {
            ClockIn clockIn = blackRegistersMap.get(item.getKey());

            int morningOutIndex = findMorningOutClockInIndex(item.getValue(), scrapingInfo.getAfternoonShiftStart());

            clockIn.setMorningIn(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), 0, "")));
            clockIn.setMorningOut(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), morningOutIndex, "")));
            clockIn.setAfternoonIn(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), morningOutIndex + 1, "")));
            clockIn.setAfternoonOut(GeneralHelpers.formatTime(GeneralHelpers.getStringFromList(item.getValue(), item.getValue().size() - 1, "")));
            clockIn.setExtraIn(GeneralHelpers.formatTime(""));
            clockIn.setExtraOut(GeneralHelpers.formatTime(""));
        }

        List<ClockIn> clockInList = new ArrayList<>(blackRegistersMap.values());

        clockInList.sort(Comparator.comparing(ClockIn::getMonthDay));

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

    public int findMorningOutClockInIndex(List<String> hours, String afternoonShiftStart) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("pt-BR"));
        LocalTime afternoonShiftStartHour = LocalTime.parse(afternoonShiftStart, timeFormatter);

        for (int index = 0; index < hours.size(); index++) {
            LocalTime hour = LocalTime.parse(hours.get(index), timeFormatter);
            if (!hour.isBefore(afternoonShiftStartHour)) {
                return index - 1;
            }
        }

        return 0;
    }

}
