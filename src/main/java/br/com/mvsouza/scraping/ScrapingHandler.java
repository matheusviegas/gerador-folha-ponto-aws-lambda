package br.com.mvsouza.scraping;

import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import br.com.mvsouza.scraping.exceptions.InvalidCredentialsException;
import br.com.mvsouza.scraping.exceptions.ScrapingException;
import br.com.mvsouza.helpers.GeneralHelpers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

@Builder
@Data
public class ScrapingHandler implements Constants {

    private ScrapingInfo scrapingInfo;
    private WebDriver driver;

    public ABSGPResponseWrapper execute() throws ScrapingException, InvalidCredentialsException {
        try {
            driver.get(URL_LOGIN_PAGE);

            WebElement txtLogin = driver.findElement(By.cssSelector(EMAIL_INPUT_CSS_SELECTOR));
            txtLogin.sendKeys(scrapingInfo.getEmail());

            WebElement txtSenha = driver.findElement(By.cssSelector(PASSWORD_INPUT_CSS_SELECTOR));
            txtSenha.sendKeys(scrapingInfo.getPassword());

            driver.findElement(By.cssSelector(LOGIN_BUTTON_CSS_SELECTOR)).click();

            this.validateCredentials(driver);

            driver.get(GeneralHelpers.buildJsonDownloadURL(scrapingInfo.getDateReference()));
            String jsonContent = driver.findElement(By.cssSelector(JSON_CONTENT_CSS_SELECTOR)).getText();

            return new ObjectMapper().readValue(jsonContent, ABSGPResponseWrapper.class);
        } catch (WebDriverException | JsonProcessingException e) {
            throw new ScrapingException("Erro ao capturar dados do ABSGP", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private void validateCredentials(WebDriver driver) throws InvalidCredentialsException {
        List<WebElement> alertBox = driver.findElements(By.cssSelector(ALERT_BOX_CSS_SELECTOR));

        if (!alertBox.isEmpty() && (alertBox.get(0).getText().contains("Invalid password!") || alertBox.get(0).getText().contains(String.format("User %s is not found!", scrapingInfo.getEmail())))) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
    }

}
