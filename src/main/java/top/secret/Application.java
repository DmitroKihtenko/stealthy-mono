package top.secret;

import com.mongodb.client.MongoClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import top.secret.threads.CleanFilesThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
})
@EnableWebMvc
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages="top.secret")
@PropertySource("classpath:application.yml")
public class Application extends SpringBootServletInitializer {
    Logger log = LoggerFactory.getLogger(Application.class);
    private CleanFilesThread cleanFilesThread;
    private ExecutorService executor;
    private MongoClient mongoClient;

    @Autowired
    public void setCleanFilesThread(CleanFilesThread cleanFilesThread) {
        this.cleanFilesThread = cleanFilesThread;
    }

    @Autowired
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Autowired
    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @PostConstruct
    public void onStartup() {
        cleanFilesThread.setDaemon(true);
        cleanFilesThread.setPriority(Thread.MIN_PRIORITY);
        executor.submit(cleanFilesThread);

        log.info("Application started");
    }

    @PreDestroy
    public void onShutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            executor.shutdownNow();
        }
        mongoClient.close();

        log.info("Application stopped");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(
                Application.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(context::close));
    }
}
