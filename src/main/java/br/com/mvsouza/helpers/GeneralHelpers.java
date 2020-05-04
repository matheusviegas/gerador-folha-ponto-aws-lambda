package br.com.mvsouza.helpers;

import br.com.mvsouza.scraping.bean.ScrapingInfo;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class GeneralHelpers {

    public static String buildJsonDownloadURL(Calendar cal) {
        Integer monthNumber = cal.get(Calendar.MONTH) + 1;
        String initialDate = String.format("%d-%s-01", cal.get(Calendar.YEAR), (monthNumber < 10 ? "0" + monthNumber : monthNumber));
        String finalDate = String.format("%d-%s-%d", cal.get(Calendar.YEAR), (monthNumber < 10 ? "0" + monthNumber : monthNumber), cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return String.format("http://gp.absoluta.net/api/user/timesheets?final_date=%s&initial_date=%s", finalDate, initialDate);
    }

    public static ScrapingInfo parseFromParams(String[] params) throws IllegalArgumentException, ParseException {
        Args.requireNonNull(params, "É necessário informar os parâmetros de entrada");
        Args.requireArraySize(params, 3, "É necessário informar email, senha e referência de data (MM/yyyy)");
        Args.requireNonEmpty(params[0], "É necessário informar o email");
        Args.requireNonEmpty(params[1], "É necessário informar a senha");
        Args.requireNonEmpty(params[2], "É necessário informar a data de referência");

        Calendar dateReference = Calendar.getInstance();
        dateReference.setTime(DateHelper.parse(params[2], DateHelper.DateFormat.MONTH_YEAR_SLASH));

        return ScrapingInfo.builder().dateReference(dateReference).email(params[0]).password(params[1]).build();
    }

    public static String getStringFromList(List<String> list, Integer index, String orElse) {
        return Optional.ofNullable(list).filter(l -> l.size() > index).map(l -> l.get(index)).orElse(orElse);
    }

    public static String formatTime(String time) {
        if (!time.contains(":")) {
            return "";
        }

        String[] splitted = time.split(":");
        return String.format("%s:%s", splitted[0], splitted[1]);
    }

}
