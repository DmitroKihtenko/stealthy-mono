package top.secret.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import top.secret.exceptions.UserError;
import top.secret.pojo.FileData;
import top.secret.pojo.config.Collections;

@Slf4j
@Service
public class FileService {
    private MongoTemplate template;

    @Autowired
    public FileService(
            @NonNull MongoTemplate mongoTemplate
    ) {
        setTemplate(mongoTemplate);
    }

    public void setTemplate(@NonNull MongoTemplate template) {
        this.template = template;
    }

    public FileData getFileById(String id) throws UserError {
        log.debug("Retrieving file data by id '" + id + "'");

        Query query = new Query();
        query.addCriteria(Criteria.where("identifier").is(id));
        FileData user = template.findOne(
                query, FileData.class, Collections.FILES.getName()
        );
        if (user == null) {
            throw new UserError("File with id '" + id + "' not found");
        }
        return user;
    }

    public boolean checkFileExists(String id) {
        log.debug("Checking file data with id '" + id + "' exists");

        Query query = new Query();
        query.fields().include("identifier");
        query.addCriteria(Criteria.where("file_id").is(id));
        return template.exists(query, Collections.FILES.getName());
    }

    public void checkFileExistsWithError(String id) throws UserError {
        if (checkFileExists(id)) {
            throw new UserError(
                    "File with id '" + id + "' already exist"
            );
        }
    }

    public void addFile(@NonNull FileData request) throws UserError {
        log.debug("Adding new file data with id '" + request.getIdentifier() +
                "'");

        checkFileExistsWithError(request.getIdentifier());
        template.save(request, Collections.FILES.getName());
    }
}
