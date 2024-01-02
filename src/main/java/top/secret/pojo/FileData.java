package top.secret.pojo;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.bson.types.Binary;

@EqualsAndHashCode
@NoArgsConstructor
public class FileData {
    @Setter
    @Getter
    @Pattern(regexp = "^[A-za-z0-9]{48}$", message = "File id should " +
            "contain only latin symbols and numbers with length 48 symbols")
    private String identifier;
    private Binary data;

    public byte[] getData() {
        return data.getData();
    }

    public void setData(byte[] data) {
        this.data = new Binary(data);
    }

    @Override
    public String toString() {
        return "FileMetadata{" +
                "identifier='" + identifier + '\'' +
                ", bytesSize='" + data.length() + '\'' +
                "}, " + super.toString();
    }
}
