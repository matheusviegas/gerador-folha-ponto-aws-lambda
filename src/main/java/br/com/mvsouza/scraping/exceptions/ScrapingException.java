package br.com.mvsouza.scraping.exceptions;

public class ScrapingException extends Exception {

    public ScrapingException(String description, Throwable throwable) {
        super(description, throwable);
    }

}
