package top.secret.pojo.schemas;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class FileIdParams {
    @Pattern(regexp = "^[A-za-z0-9]{48}$", message = "File id should " +
            "contain only latin symbols and numbers with length 48 symbols")
    private String fileId;
}
