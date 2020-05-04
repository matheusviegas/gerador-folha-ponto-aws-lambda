package br.com.mvsouza.helpers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeleniumHelper {

    public static WebDriver getWebDriver() {
        //setFilePermissions();
        System.setProperty("webdriver.chrome.driver", getLibLocation("chromedriver"));

        ChromeOptions options = new ChromeOptions();
        options.setBinary(getLibLocation("chrome"));
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

    private static String getLibLocation(String lib) {
        return String.format("%s/lib/%s", System.getenv("LAMBDA_TASK_ROOT"), lib);
    }

    private static void setFilePermissions() {
        try {
            File chrome = new File(getLibLocation("chrome"));
            chrome.setExecutable(true, false);
            chrome.setReadable(true, false);
            chrome.setWritable(true, false);
            File chromeDriver = new File(getLibLocation("chromedriver"));
            chromeDriver.setExecutable(true, false);
            chromeDriver.setReadable(true, false);
            chromeDriver.setWritable(true, false);
        } catch (Exception e) {
            Logger.getLogger(SeleniumHelper.class.getSimpleName()).log(Level.SEVERE, "Erro ao alterar permissões dos executaveis", e);
        }
    }

}