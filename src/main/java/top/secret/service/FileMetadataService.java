package top.secret.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.secret.exceptions.UserError;
import top.secret.pojo.FileMetadata;
import top.secret.pojo.config.Collections;
import top.secret.pojo.config.FilesConfig;
import top.secret.pojo.schemas.FilesMetaListResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.UUID;

@Slf4j
@Service
@PropertySource("classpath:application.yml")
public class FileMetadataService {

    @Setter
    private MongoTemplate template;

    @Getter
    private FilesConfig filesConfig;

    @Autowired
    public FileMetadataService(
            @NonNull MongoTemplate mongoTemplate
    ) {
        setTemplate(mongoTemplate);
        filesConfig = new FilesConfig();
    }

    @Autowired
    public void setFilesConfig(@NonNull FilesConfig filesConfig) {
        this.filesConfig = filesConfig;
    }

    public FileMetadata getFileMetadataById(String id) throws UserError {
        log.debug("Retrieving file metadata by id '" + id + "'");

        Query query = new Query();
        query.addCriteria(Criteria.where("identifier").is(id));
        FileMetadata metadata = template.findOne(query, FileMetadata.class,
                Collections.FILES_METADATA.getName());
        if (metadata == null) {
            throw new UserError("File with id '" + id +
                    "' not found");
        }
        return metadata;
    }

    public boolean checkFileMetadataExists(String id) {
        log.debug("Checking file metadata with id '" + id + "' exists");

        Query query = new Query();
        query.fields().include("identifier");
        query.addCriteria(Criteria.where("identifier").is(id));
        return template.exists(query, Collections.FILES_METADATA.getName());
    }

    public FilesMetaListResponse getFileMetadataList(
            String username, int page
    ) throws UserError {
        log.debug("Retrieving file metadata list for user '" +
                username + "' and page '" + page + "'");

        FilesMetaListResponse result = new FilesMetaListResponse();

        Bson filter = Filters.eq("username", username);
        Bson sort = Sorts.descending("creation");

        MongoCollection<Document> collection = template.getCollection(
                Collections.FILES_METADATA.getName());

        result.setTotal((int) collection.countDocuments(filter));
        result.setRecords(new LinkedList<>());

        int skip = (page - 1) * filesConfig.getPerPageLimit();

        try (MongoCursor<FileMetadata> documents = collection.find(
                filter, FileMetadata.class)
                .sort(sort)
                .limit(filesConfig.getPerPageLimit())
                .skip(skip).iterator()) {
            while (documents.hasNext()) {
                result.getRecords().add(documents.next());
            }
        }

        if (result.getRecords().isEmpty() && page > 1) {
            throw new UserError(
                    "There are no files data for page " + page);
        }

        return result;
    }

    public void addFileMetadata(
            @NonNull FileMetadata request
    ) throws UserError {
        log.debug("Adding new file metadata with id '" +
                request.getIdentifier() + "'");

        if (checkFileMetadataExists(request.getIdentifier())) {
            throw new UserError("File with id '" + request.getIdentifier() +
                    "' already exist");
        }
        template.save(request, Collections.FILES_METADATA.getName());
    }

    public String generateFileId() {
        log.debug("Generating new file id");

        UUID uuid = UUID.randomUUID();
        byte[] bytes = uuid.toString().getBytes(StandardCharsets.UTF_8);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
