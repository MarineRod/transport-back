package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.MailSendingSettingsDTO;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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
    private String username;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendHtmlMail(MailSendingSettingsDTO mailSendingSettings) throws Exception {
   
            MimeMessage message = javaMailSender.createMimeMessage();
            Context context = new Context();
            if (mailSendingSettings.getContextData() != null)
                mailSendingSettings.getContextData().forEach(context::setVariable);
            String process = templateEngine.process(mailSendingSettings.getMailTemplateEnum().toString(), context);
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(mailSendingSettings.getTo());
            helper.setFrom(new InternetAddress(username));
            helper.setSubject(mailSendingSettings.getSubject());
            helper.setText(process, true);
            javaMailSender.send(message);
        

    }



}
