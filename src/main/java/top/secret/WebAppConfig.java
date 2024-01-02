package top.secret;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.connection.ConnectionPoolSettings;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mongo.*;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mapping.model.SnakeCaseFieldNamingStrategy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(1);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver =
                new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public CodecRegistry codecRegistry() {
        return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    @Bean
    PropertiesMongoConnectionDetails mongoConnectionDetails(
            MongoProperties properties
    ) {
        return new PropertiesMongoConnectionDetails(properties);
    }

    @Bean
    StandardMongoClientSettingsBuilderCustomizer settingsCustomizer(
            MongoProperties properties,
            MongoConnectionDetails connectionDetails,
            ObjectProvider<SslBundles> sslBundles
    ) {
        return new StandardMongoClientSettingsBuilderCustomizer(
                connectionDetails.getConnectionString(),
                properties.getUuidRepresentation(),
                properties.getSsl(),
                sslBundles.getIfAvailable()
        );
    }

    @Bean
    MongoClientFactory mongoClientFactory(
            MongoClientSettingsBuilderCustomizer customizer
    ) {
        LinkedList<MongoClientSettingsBuilderCustomizer> customizers =
                new LinkedList<>();
        customizers.add(customizer);
        return new MongoClientFactory(customizers);
    }

    @Bean
    public MongoClient mongoClient(
            MongoClientFactory clientFactory,
            CodecRegistry codecRegistry
    ) {
        ConnectionPoolSettings connectionPoolSettings =
                ConnectionPoolSettings.builder()
                .maxWaitTime(5000, TimeUnit.MILLISECONDS)
                .maxSize(30)
                .minSize(10)
                .maxConnectionLifeTime(60000, TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(30000, TimeUnit.MILLISECONDS)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(codecRegistry)
                .applyToConnectionPoolSettings(builder -> builder.
                        applySettings(connectionPoolSettings))
                .build();

        return clientFactory.createMongoClient(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(
            MongoClient client,
            MongoProperties properties
    ) {
        return new SimpleMongoClientDatabaseFactory(
                client, properties.getDatabase()
        );
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        MongoMappingContext context = new MongoMappingContext();
        context.setFieldNamingStrategy(new SnakeCaseFieldNamingStrategy());
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(factory), context
        );
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            ExceptionFilter exceptionFilter
    ) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(
                customizer -> customizer.
                        requestMatchers(
                                "/static/**",
                                "/WEB-INF/pages/**",
                                "/sign-up",
                                "/sign-in",
                                "/download",
                                "/error-page"
                        ).permitAll().
                        anyRequest().authenticated()
        );

        httpSecurity.formLogin(
                AbstractHttpConfigurer::disable
        );
        httpSecurity.logout(
                logout -> logout.logoutRequestMatcher(
                        new AntPathRequestMatcher("/sign-out")
                ).logoutSuccessUrl("/download")
        );

        httpSecurity.addFilterAfter(
                exceptionFilter, ExceptionTranslationFilter.class
        );

        return httpSecurity.build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").
                addResourceLocations("/WEB-INF/static/");
    }
}
