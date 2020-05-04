package br.com.mvsouza.enumeration;

import java.util.stream.Stream;

public enum Month {
    JANUARY("Janeiro", "01"), FEBRUARY("Fevereiro", "02"), MARCH("Março", "03"), APRIL("Abril", "04"),
    MAY("Maio", "05"), JUNE("Junho", "06"), JULY("Julho", "07"), AUGUST("Agosto", "08"),
    SEPTEMBER("Setembro", "09"), OCTOBER("Outubro", "10"), NOVEMBER("Novembro", "11"), DECEMBER("Dezembro", "12");

    private final String description;
    private final String number;

    Month(String description, String number) {
        this.description = description;
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public String getNumber() {
        return number;
    }

    public static Month fromDescription(String description) {
        return Stream.of(values()).filter(m -> m.getDescription().equals(description)).findFirst().orElse(null);
    }

}
