package top.secret.threads;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import top.secret.pojo.config.Collections;
import top.secret.pojo.config.FilesConfig;
import top.secret.pojo.config.InstitutionConfig;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Setter
@Getter
@AllArgsConstructor
public class CleanFilesThread extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(CleanFilesThread.class);

    private MongoTemplate mongoTemplate;

    private InstitutionConfig institutionInfo;

    private FilesConfig filesConfig;

    public void run() {
        try {
            logger.info("Files cleaning thread started");
            runCleanupCycle();
        } catch (Exception e) {
            logger.info("Files cleaning thread stopped");
        }
    }

    public void runCleanupCycle() throws InterruptedException {
        while (true) {
            try {
                doCleanup();
            } catch (Exception e) {
                logger.error("Files cleaning error: " + e.getMessage());
            }
            sleep(filesConfig.getCleanupSecondsPeriod() * 1000L);
        }
    }

    public void doCleanup() {
        logger.info("Files cleaning started");

        MongoCollection<Document> filesCollection = mongoTemplate.
                getCollection(Collections.FILES.getName());
        MongoCollection<Document> filesMetadataCollection = mongoTemplate.
                getCollection(Collections.FILES_METADATA.getName());

        long nowTimestamp = LocalDateTime.now().
                toEpochSecond(ZoneOffset.UTC);

        filesMetadataCollection.deleteMany(
                Filters.lte("expiration", nowTimestamp)
        );
        DeleteResult result = filesCollection.deleteMany(
                Filters.lte("expiration", nowTimestamp)
        );

        logger.info("Cleaned up "+ result.getDeletedCount() + " files");
    }
}
