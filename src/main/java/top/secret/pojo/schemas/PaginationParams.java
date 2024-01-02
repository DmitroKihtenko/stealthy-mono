package top.secret.pojo.schemas;


import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PaginationParams {
    @Positive(message = "Page number should be positive value")
    private int page;
}
