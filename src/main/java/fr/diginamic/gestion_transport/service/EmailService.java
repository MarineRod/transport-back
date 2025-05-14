package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.MailSendingSettingsDTO;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String USERNAME;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendHtmlMail(MailSendingSettingsDTO mailSendingSettings) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            Context context = new Context();
            if (mailSendingSettings.getContextData() != null)
                mailSendingSettings.getContextData().forEach(context::setVariable);
            String process = templateEngine.process(mailSendingSettings.getMailTemplateEnum().toString(), context);
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(mailSendingSettings.getTo());
            helper.setFrom(new InternetAddress(USERNAME));
            helper.setSubject(mailSendingSettings.getSubject());
            helper.setText(process, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new Exception(e.getMessage());

        }

    }

    public void sendTextMail(String to) throws Exception {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            Context context = new Context();
            context.setVariable("username", USERNAME);
            String process = templateEngine.process("email_text.txt", context);
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setSubject("TEXT email from Spring Boot");
            helper.setFrom(new InternetAddress(USERNAME));
            helper.setTo(to);
            helper.setText(process);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


}
