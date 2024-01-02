package top.secret.pojo;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class FileMetadata {
    @Pattern(regexp = "^[A-za-z0-9]{48}$", message = "File id should " +
            "contain only latin symbols and numbers with length 48 symbols")
    private String identifier;

    @Pattern(regexp = "^[^<>:\"/\\\\|?*]{1,200}$", message =
            "File name should not contain symbols <>:\"\\/|?* and should " +
                    " length 1-200")
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,24}$", message = "Username can only " +
            "contain latin symbols, numbers, symbols _- with length 4-24")
    private String username;
    private long size;

    @Size(max = 100, message = "File mimetype length should not be more than " +
            "60 symbols")
    private String mimetype;
    private long creation;

    private long expiration;
}
