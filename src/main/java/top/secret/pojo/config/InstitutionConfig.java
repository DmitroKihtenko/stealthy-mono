package top.secret.pojo.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "institution")
@Getter
@Data
@EqualsAndHashCode
@ToString
public class InstitutionConfig {
    private String name;
    private String description;
    private String link;
    private int timeZoneHoursOffset;

    public InstitutionConfig() {
        name = "Stealthy";
        description="Web application for secure sharing" +
                " sensitive information";
        timeZoneHoursOffset = 2;
    }
}
