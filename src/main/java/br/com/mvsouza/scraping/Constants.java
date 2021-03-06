package br.com.mvsouza.scraping;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    public static final String URL_LOGIN_PAGE = "http://absgp.deliverit.com.br/login";
    public static final String EMAIL_INPUT_CSS_SELECTOR = "input[name=email]";
    public static final String PASSWORD_INPUT_CSS_SELECTOR = "input[name=password]";
    public static final String LOGIN_BUTTON_CSS_SELECTOR = "button[type=submit";
    public static final String ALERT_BOX_CSS_SELECTOR = "div.alert-danger";
    public static final String JSON_CONTENT_CSS_SELECTOR = "body > pre";

}


