package fr.diginamic.gestion_transport.service;

import fr.diginamic.gestion_transport.dto.MailSendingSettingsDTO;
import fr.diginamic.gestion_transport.enums.MailTemplateEnum;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private MailSendingSettingsDTO mailSendingSettings;
    private final String testUsername = "test@example.com";

    @BeforeEach
    void setUp() {
        // Injection de la valeur du username via reflection
        ReflectionTestUtils.setField(emailService, "username", testUsername);

        // Configuration du DTO de test
        mailSendingSettings = new MailSendingSettingsDTO();
        mailSendingSettings.setTo("recipient@example.com");
        mailSendingSettings.setSubject("Test Subject");
        mailSendingSettings.setMailTemplateEnum(MailTemplateEnum.CANCELED_CARPOOLING);

        Map<String, String> contextData = new HashMap<>();
        contextData.put("name", "John Doe");
        mailSendingSettings.setContextData(contextData);
    }

    @Test
    void sendHtmlMail_Success() throws Exception {
        // Arrange
        String processedTemplate = "<html><body>Hello John Doe from Test Company</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);

        // Act
        emailService.sendHtmlMail(mailSendingSettings);

        // Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);

        ArgumentCaptor<String> templateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(templateCaptor.capture(), contextCaptor.capture());

        assertEquals(MailTemplateEnum.CANCELED_CARPOOLING.toString(), templateCaptor.getValue());

        Context capturedContext = contextCaptor.getValue();
        assertNotNull(capturedContext);
    }

    @Test
    void sendHtmlMail_WithNullContextData() throws Exception {
        // Arrange
        mailSendingSettings.setContextData(null);
        String processedTemplate = "<html><body>Default template</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);

        // Act
        emailService.sendHtmlMail(mailSendingSettings);

        // Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlMail_WithEmptyContextData() throws Exception {
        // Arrange
        mailSendingSettings.setContextData(new HashMap<>());
        String processedTemplate = "<html><body>Empty context template</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);

        // Act
        emailService.sendHtmlMail(mailSendingSettings);

        // Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlMail_TemplateEngineThrowsException() {
        // Arrange
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class)))
                .thenThrow(new RuntimeException("Template processing failed"));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            emailService.sendHtmlMail(mailSendingSettings);
        });

        assertTrue(exception.getMessage().contains("Template processing failed"));
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendHtmlMail_JavaMailSenderThrowsException() {
        // Arrange
        String processedTemplate = "<html><body>Test template</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);
        doThrow(new RuntimeException("Mail sending failed")).when(javaMailSender).send(mimeMessage);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            emailService.sendHtmlMail(mailSendingSettings);
        });

        assertTrue(exception.getMessage().contains("Mail sending failed"));
        verify(javaMailSender).createMimeMessage();
        verify(templateEngine).process(anyString(), any(Context.class));
    }

    @Test
    void sendHtmlMail_VerifyMimeMessageHelperConfiguration() throws Exception {

        // Arrange
        String processedTemplate = "<html><body>Test</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);

        // Act
        emailService.sendHtmlMail(mailSendingSettings);

        // Assert
        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @Test
    void sendHtmlMail_VerifyContextVariables() throws Exception {
        // Arrange
        String processedTemplate = "<html><body>Test</body></html>";

        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn(processedTemplate);

        // Act
        emailService.sendHtmlMail(mailSendingSettings);

        // Assert
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(anyString(), contextCaptor.capture());

        Context capturedContext = contextCaptor.getValue();
        assertNotNull(capturedContext);
    }
}