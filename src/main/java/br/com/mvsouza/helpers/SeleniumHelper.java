package br.com.mvsouza.helpers;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@UtilityClass
public class SeleniumHelper {

    public WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", getResourceLocation("chromedriver"));

        ChromeOptions options = new ChromeOptions();
        options.setBinary(getResourceLocation("chrome"));
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--single-process");
        options.addArguments("--user-data-dir=/tmp/user-data");
        options.addArguments("--data-path=/tmp/data-path");
        options.addArguments("--homedir=/tmp");
        options.addArguments("--disk-cache-dir=/tmp/cache-dir");
        return new ChromeDriver(options);
    }

    private String getResourceLocation(String resource) {
        return String.format("/opt/java/lib/%s", resource);
    }

}