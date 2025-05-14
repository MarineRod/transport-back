package fr.diginamic.gestion_transport.dto;

import fr.diginamic.gestion_transport.enums.MailTemplateEnum;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailSendingSettingsDTO {
    String to;
    MailTemplateEnum mailTemplateEnum;
    Map<String, String> contextData;
    String subject;
}
