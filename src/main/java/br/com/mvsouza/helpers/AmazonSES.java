package br.com.mvsouza.helpers;


import br.com.mvsouza.lambda.bean.GenerateReportRequest;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonSES {
    private GenerateReportRequest requestInfo;
    private byte[] reportContent;

    private static String SENDER = "Matheus Souza <contato@mvsouza.com.br>";
    private static String SUBJECT = "Gerador de Planilha de Folha Ponto";
    private static String BODY_TEXT = "Segue em anexo a planilha de folha ponto gerada";

    private void setUpAWSCredentials() {
        System.setProperty("aws.accessKeyId", "");
        System.setProperty("aws.secretKey", "");
    }

    public void sendEmail() throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setSubject(SUBJECT, "UTF-8");
        message.setFrom(new InternetAddress(SENDER));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requestInfo.getEmail()));

        MimeMultipart messageBody = new MimeMultipart("alternative");

        MimeBodyPart wrap = new MimeBodyPart();

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(BODY_TEXT, "text/plain; charset=UTF-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(BODY_TEXT, "text/html; charset=UTF-8");

        messageBody.addBodyPart(textPart);
        messageBody.addBodyPart(htmlPart);

        wrap.setContent(messageBody);

        MimeMultipart msg = new MimeMultipart("mixed");

        message.setContent(msg);

        msg.addBodyPart(wrap);

        MimeBodyPart att = new MimeBodyPart();

        DataSource byteArrayDataSource = new ByteArrayDataSource(reportContent, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        att.setDataHandler(new DataHandler(byteArrayDataSource));
        att.setFileName("folha-ponto.xlsx");

        msg.addBodyPart(att);

        try {
            setUpAWSCredentials();
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withCredentials(new SystemPropertiesCredentialsProvider()).withRegion(Regions.US_EAST_1).build();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);

            client.sendRawEmail(rawEmailRequest);
        } catch (Exception ex) {
            Logger.getLogger(AmazonSES.class.getSimpleName()).log(Level.SEVERE, "Erro ao enviar email.", ex);
            throw new Exception("Erro ao enviar email.");
        }
    }
}
