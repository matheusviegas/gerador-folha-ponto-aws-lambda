package br.com.mvsouza;

import br.com.mvsouza.excel.ExcelReportGenerator;
import br.com.mvsouza.helpers.GeneralHelpers;
import br.com.mvsouza.helpers.SeleniumHelper;
import br.com.mvsouza.scraping.ScrapingHandler;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import br.com.mvsouza.view.Application;
import org.openqa.selenium.WebDriver;

import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        if (args.length == 3) {
            try {
                ScrapingInfo info = GeneralHelpers.parseFromParams(args);
                WebDriver driver = SeleniumHelper.getWebDriver();

                ABSGPResponseWrapper response = ScrapingHandler.builder().driver(driver).scrapingInfo(info).build().execute();

                byte[] reportContent = ExcelReportGenerator.builder().absgpData(response).scrapingInfo(info).build().generateReport();
                FileOutputStream fileOut = new FileOutputStream("planilha.xlsx");
                fileOut.write(reportContent);
                fileOut.close();
            } catch (Exception e) {
                Logger.getLogger(Main.class.getSimpleName()).log(Level.SEVERE, e.getMessage());
            }
        } else {
            new Application().setVisible(true);
        }
    }

}
