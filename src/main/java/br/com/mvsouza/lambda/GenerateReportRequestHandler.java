package br.com.mvsouza.lambda;

import br.com.mvsouza.excel.ExcelReportGenerator;
import br.com.mvsouza.helpers.AmazonSES;
import br.com.mvsouza.helpers.DateHelper;
import br.com.mvsouza.helpers.JSON;
import br.com.mvsouza.helpers.SeleniumHelper;
import br.com.mvsouza.lambda.bean.GenerateReportRequest;
import br.com.mvsouza.scraping.ScrapingHandler;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateReportRequestHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        try {
            List<SQSEvent.SQSMessage> messageList = event.getRecords();

            if (messageList.isEmpty()) {
                Logger.getLogger(GenerateReportRequestHandler.class.getSimpleName()).log(Level.INFO, "Lista de mensagens vazia.");
                return null;
            }

            SQSEvent.SQSMessage message = messageList.get(0);

            GenerateReportRequest requestDTO = JSON.fromJson(message.getBody(), GenerateReportRequest.class);

            if (requestDTO == null) {
                Logger.getLogger(GenerateReportRequestHandler.class.getSimpleName()).log(Level.INFO, "DTO nulo. Erro ao deserializar.");
                return null;
            }

            ScrapingInfo info = ScrapingInfo.builder().email(requestDTO.getEmail()).password(requestDTO.getPassword()).name(requestDTO.getName()).dateReference(DateHelper.parseDateReference(requestDTO.getDateReference())).build();
            WebDriver driver = SeleniumHelper.getWebDriver();

            ABSGPResponseWrapper scrapingResponse = ScrapingHandler.builder().driver(driver).scrapingInfo(info).build().execute();

            byte[] reportContent = ExcelReportGenerator.builder().absgpData(scrapingResponse).scrapingInfo(info).build().generateReport();

            AmazonSES.builder().reportContent(reportContent).requestInfo(requestDTO).build().sendEmail();
        } catch (Exception e) {
            Logger.getLogger(GenerateReportRequestHandler.class.getSimpleName()).log(Level.SEVERE, "Erro geral ao gerar planilha.", e);
        }

        return null;
    }

}
