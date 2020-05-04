package br.com.mvsouza.helpers;

import br.com.mvsouza.enumeration.Month;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ViewHelper {

    public static DefaultComboBoxModel buildMonthsComboBoxModel() {
        List<String> months = Stream.of(Month.values()).map(Month::getDescription).collect(Collectors.toList());
        return new DefaultComboBoxModel(months.toArray(new String[0]));
    }

    public static DefaultComboBoxModel buildYearsComboBoxModel() {
        List<String> years = new ArrayList<>();

        Integer currentYear = new Date().getYear() + 1900;

        int index = 0;
        for (int i = currentYear; i >= 2015; i--) {
            years.add(String.valueOf(i));
        }

        return new DefaultComboBoxModel(years.toArray(new String[0]));
    }

    public static String buildDateReferenceFromView(String monthLabel, String year) {
        Month month = Month.fromDescription(monthLabel);
        return String.format("%s/%s", month.getNumber(), year);
    }

}
