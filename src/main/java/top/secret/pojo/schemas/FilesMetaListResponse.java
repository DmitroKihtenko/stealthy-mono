package top.secret.pojo.schemas;

import lombok.*;
import top.secret.pojo.FileMetadata;

import java.util.LinkedList;
import java.util.List;

@Getter
@Data
@EqualsAndHashCode
@ToString
public class FilesMetaListResponse {
    private List<FileMetadata> records;
    private int total;

    public FilesMetaListResponse() {
        records = new LinkedList<>();
        total = 0;
    }
}
