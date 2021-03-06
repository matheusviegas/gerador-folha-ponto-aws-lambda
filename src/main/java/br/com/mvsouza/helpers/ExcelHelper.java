package br.com.mvsouza.helpers;

import br.com.mvsouza.excel.bean.ClockIn;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelHelper {

    private List<ClockIn> clockIns;
    private ScrapingInfo scrapingInfo;

    public byte[] generateContent() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cálculo Horas");
            sheet.setDisplayGridlines(false);

            Font headerFontNotBold = workbook.createFont();
            headerFontNotBold.setBold(false);
            headerFontNotBold.setFontHeightInPoints((short) 11);
            headerFontNotBold.setFontName("Calibri");

            Font arial = workbook.createFont();
            arial.setBold(false);
            arial.setFontHeightInPoints((short) 8);
            arial.setFontName("Arial");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setFontName("Calibri");

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);

            CellStyle leftStyle = workbook.createCellStyle();
            leftStyle.setAlignment(HorizontalAlignment.LEFT);
            leftStyle.setFont(headerFontNotBold);
            leftStyle.setBorderBottom(BorderStyle.THIN);
            leftStyle.setBorderLeft(BorderStyle.THIN);
            leftStyle.setBorderRight(BorderStyle.THIN);
            leftStyle.setBorderTop(BorderStyle.THIN);

            CellStyle centerStyleArial = workbook.createCellStyle();
            centerStyleArial.setFont(arial);
            centerStyleArial.setAlignment(HorizontalAlignment.CENTER);
            centerStyleArial.setBorderBottom(BorderStyle.THIN);
            centerStyleArial.setBorderLeft(BorderStyle.THIN);
            centerStyleArial.setBorderRight(BorderStyle.THIN);
            centerStyleArial.setBorderTop(BorderStyle.THIN);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setFont(headerFontNotBold);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setBorderBottom(BorderStyle.THIN);
            centerStyle.setBorderLeft(BorderStyle.THIN);
            centerStyle.setBorderRight(BorderStyle.THIN);
            centerStyle.setBorderTop(BorderStyle.THIN);

            final String ENTRADA_HEADER_NAME = "Entrada";
            final String SAIDA_HEADER_NAME = "Saída";
            String[] headers = new String[]{"1º Turno", "2º Turno", "Extra", "Assinatura"};
            String[] subHeaders = new String[]{"Data", "", ENTRADA_HEADER_NAME, SAIDA_HEADER_NAME, ENTRADA_HEADER_NAME, SAIDA_HEADER_NAME, ENTRADA_HEADER_NAME, SAIDA_HEADER_NAME, ""};

            Row topo = sheet.createRow(0);

            CellStyle topoStyle = workbook.createCellStyle();
            topoStyle.setFont(headerFontNotBold);

            Cell nomeCell = topo.createCell(0);
            nomeCell.setCellValue(String.format("Nome do Colaborador: %s", scrapingInfo.getName()));
            nomeCell.setCellStyle(topoStyle);

            Cell mes = topo.createCell(7);
            mes.setCellValue(DateHelper.getMonthName(scrapingInfo.getDateReference()));
            mes.setCellStyle(topoStyle);

            Cell ano = topo.createCell(8);
            ano.setCellValue(String.valueOf(scrapingInfo.getDateReference().get(Calendar.YEAR)));
            ano.setCellStyle(topoStyle);

            Row topheader = sheet.createRow(2);

            sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 1));

            Cell cell2 = CellUtil.createCell(topheader, 0, "Data");
            cell2.setCellStyle(centerStyle);
            CellUtil.setAlignment(cell2, HorizontalAlignment.CENTER_SELECTION);

            Cell primeiroTurno = topheader.createCell(2);
            primeiroTurno.setCellValue(headers[0]);
            primeiroTurno.setCellStyle(headerCellStyle);

            Cell segundoTurno = topheader.createCell(4);
            segundoTurno.setCellValue(headers[1]);
            segundoTurno.setCellStyle(headerCellStyle);

            Cell extra = topheader.createCell(6);
            extra.setCellValue(headers[2]);
            extra.setCellStyle(headerCellStyle);

            Cell assinatura = topheader.createCell(8);
            assinatura.setCellValue(headers[3]);
            assinatura.setCellStyle(headerCellStyle);


            sheet.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 6, 7));

            Row headerRow = sheet.createRow(3);

            for (int i = 1; i < subHeaders.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(subHeaders[i]);
                cell.setCellStyle(centerStyle);
                CellUtil.setAlignment(cell, HorizontalAlignment.CENTER_SELECTION);
            }

            int initialRow = 4;

            for (ClockIn clockIn : clockIns) {
                Row row = sheet.createRow(initialRow++);

                createCellContent(clockIn.getWeekDay(), leftStyle, row, 0, false);
                createCellContent(clockIn.getMonthDay().toString(), centerStyle, row, 1, false);
                createCellContent(clockIn.getMorningIn(), centerStyleArial, row, 2, true);
                createCellContent(clockIn.getMorningOut(), centerStyleArial, row, 3, true);
                createCellContent(clockIn.getAfternoonIn(), centerStyleArial, row, 4, true);
                createCellContent(clockIn.getAfternoonOut(), centerStyleArial, row, 5, true);
                createCellContent(clockIn.getExtraIn(), centerStyleArial, row, 6, true);
                createCellContent(clockIn.getExtraOut(), centerStyleArial, row, 7, true);
                createCellContent("", centerStyle, row, 8, true);

                if (clockIn.isSunday()) {
                    row = sheet.createRow(initialRow++);
                    createBlankLineGreyBackground(row, workbook);
                }
            }

            sheet.autoSizeColumn(1);

            sheet.setColumnWidth(0, 3500);
            sheet.setColumnWidth(8, 6000);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            baos.close();
            return baos.toByteArray();
        }
    }

    private Cell createCellContent(String content, CellStyle style, Row row, int index, boolean center) {
        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(content);
        if (center) {
            CellUtil.setAlignment(cell, HorizontalAlignment.CENTER_SELECTION);
        }
        return cell;
    }

    private void createBlankLineGreyBackground(Row row, Workbook wb) {
        CellStyle style = wb.createCellStyle();

        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        for (int i = 0; i < 9; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue("");
        }
    }

}
