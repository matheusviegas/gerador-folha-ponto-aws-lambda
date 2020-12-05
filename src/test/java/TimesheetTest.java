import br.com.mvsouza.excel.ExcelReportGenerator;
import br.com.mvsouza.excel.bean.ClockIn;
import br.com.mvsouza.helpers.DateHelper;
import br.com.mvsouza.scraping.bean.ABSGPClockInItem;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class TimesheetTest {

    @Test
    public void shouldBeAbleToFindTheCorrectShiftTimes() throws Exception {
        ScrapingInfo scrapingInfoMock = mockScrapingInfo();
        ABSGPResponseWrapper absgpResponseMock = mockAbsgpData();

        ExcelReportGenerator generator = ExcelReportGenerator.builder().scrapingInfo(scrapingInfoMock).absgpData(absgpResponseMock).build();

        List<ClockIn> timesheet = generator.parseABSGPResponse();

        ClockIn day5 = timesheet.stream().filter(clockIn -> clockIn.getMonthDay().equals(5)).findFirst().orElseThrow(Exception::new);

        Assert.assertEquals("08:00", day5.getMorningIn());
        Assert.assertEquals("12:35", day5.getMorningOut());
        Assert.assertEquals("13:45", day5.getAfternoonIn());
        Assert.assertEquals("17:53", day5.getAfternoonOut());
    }

    private ABSGPResponseWrapper mockAbsgpData() {
        ABSGPClockInItem manha = ABSGPClockInItem.builder().date("2020-12-05").initialTime("08:00").finalTime("12:35").build();
        ABSGPClockInItem tarde = ABSGPClockInItem.builder().date("2020-12-05").initialTime("13:45").finalTime("17:53").build();

        return ABSGPResponseWrapper.builder().timesheets(Arrays.asList(manha, tarde)).build();
    }

    private ScrapingInfo mockScrapingInfo() throws ParseException {
        return ScrapingInfo.builder()
                .email("matheus.souza@deliverit.com.br")
                .password("")
                .name("Matheus")
                .dateReference(DateHelper.parseDateReference("12/2020"))
                .afternoonShiftStart("13:00")
                .build();
    }

}
