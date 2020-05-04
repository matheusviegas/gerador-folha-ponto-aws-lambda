package br.com.mvsouza.lambda;

import br.com.mvsouza.excel.ExcelReportGenerator;
import br.com.mvsouza.helpers.AmazonSES;
import br.com.mvsouza.helpers.DateHelper;
import br.com.mvsouza.helpers.JSON;
import br.com.mvsouza.helpers.SeleniumHelper;
import br.com.mvsouza.lambda.bean.GenerateReportRequest;
import br.com.mvsouza.lambda.bean.GenerateReportResponse;
import br.com.mvsouza.scraping.ScrapingHandler;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.openqa.selenium.WebDriver;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateReportRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Collections.singletonMap("Author", "github.com/matheusviegas"));

        try {
            GenerateReportRequest requestDTO = JSON.fromJson(request.getBody(), GenerateReportRequest.class);

            ScrapingInfo info = ScrapingInfo.builder().email(requestDTO.getEmail()).password(requestDTO.getPassword()).name(requestDTO.getName()).dateReference(DateHelper.parseDateReference(requestDTO.getDateReference())).build();
            WebDriver driver = SeleniumHelper.getWebDriver();

            ABSGPResponseWrapper scrapingResponse = ScrapingHandler.builder().driver(driver).scrapingInfo(info).build().execute();

            byte[] reportContent = ExcelReportGenerator.builder().absgpData(scrapingResponse).scrapingInfo(info).build().generateReport();

            AmazonSES.builder().reportContent(reportContent).requestInfo(requestDTO).build().sendEmail();

            response.setStatusCode(200);
            response.setBody(JSON.toJson(GenerateReportResponse.builder().success(true).message("Planilha gerada com sucesso e enviada para o email informado.").build()));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody(JSON.toJson(GenerateReportResponse.builder().success(false).message("Erro ao gerar planilha.").build()));
            Logger.getLogger(GenerateReportRequestHandler.class.getSimpleName()).log(Level.SEVERE, "Erro geral ao gerar planilha.", e);
        }

        return response;
    }


}
