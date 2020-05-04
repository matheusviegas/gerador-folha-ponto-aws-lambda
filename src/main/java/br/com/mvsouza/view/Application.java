package br.com.mvsouza.view;

import br.com.mvsouza.excel.ExcelReportGenerator;
import br.com.mvsouza.helpers.GeneralHelpers;
import br.com.mvsouza.helpers.SeleniumHelper;
import br.com.mvsouza.helpers.ViewHelper;
import br.com.mvsouza.scraping.ScrapingHandler;
import br.com.mvsouza.scraping.bean.ABSGPResponseWrapper;
import br.com.mvsouza.scraping.bean.ScrapingInfo;
import org.openqa.selenium.WebDriver;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;


public class Application extends JFrame implements ActionListener {

    private JPanel panel1;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnGenerateReport;
    private JPanel combos;
    private JComboBox cmbMonth;
    private JComboBox cmbYear;

    public Application() {
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setContentPane(panel1);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Gerador de Folha Ponto DeliverIT");

        cmbMonth.setModel(ViewHelper.buildMonthsComboBoxModel());
        cmbYear.setModel(ViewHelper.buildYearsComboBoxModel());
        btnGenerateReport.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String email = txtEmail.getText();
            String pass = String.valueOf(txtPassword.getPassword());
            String month = cmbMonth.getSelectedItem().toString();
            String year = cmbYear.getSelectedItem().toString();

            String[] params = {email, pass, ViewHelper.buildDateReferenceFromView(month, year)};
            ScrapingInfo info = GeneralHelpers.parseFromParams(params);
            WebDriver driver = SeleniumHelper.getWebDriver();

            ABSGPResponseWrapper response = ScrapingHandler.builder().driver(driver).scrapingInfo(info).build().execute();

            byte[] reportContent = ExcelReportGenerator.builder().absgpData(response).scrapingInfo(info).build().generateReport();


            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setDialogType(JFileChooser.SAVE_DIALOG);
            fileSaver.setDialogTitle("Selecione o local para salvar o arquivo");
            fileSaver.setSelectedFile(new File("planilha.xlsx"));
            fileSaver.setFileFilter(new FileNameExtensionFilter("Planilhas Excel", "xlsx"));

            int userSelection = fileSaver.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileSaver.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());

                FileOutputStream fileOut = new FileOutputStream(fileToSave.getAbsolutePath());
                fileOut.write(reportContent);
                fileOut.close();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
}
