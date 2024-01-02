package top.secret.pojo.schemas;

import jakarta.validation.constraints.Pattern;
import lombok.*;


@Getter
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserRequest {
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,24}$", message = "Username can only " +
            "contain latin symbols, numbers, symbols '_-' with length 4-24")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9_!@#$%^&*]{8,24}$", message = "Password can" +
            " only contain latin symbols, numbers, symbols '_!@#$%^&*' with " +
            "length 8-24")
    private String password;
}
