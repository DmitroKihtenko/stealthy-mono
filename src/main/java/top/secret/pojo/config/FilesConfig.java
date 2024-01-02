package top.secret.pojo.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "files")
@Getter
@Data
@EqualsAndHashCode
@ToString
public class FilesConfig {
    private int perPageLimit;
    private int expirationMinutes;
    private int cleanupSecondsPeriod;

    public FilesConfig() {
        perPageLimit = 6;
        expirationMinutes = 20;
        cleanupSecondsPeriod = 60;
    }
}
