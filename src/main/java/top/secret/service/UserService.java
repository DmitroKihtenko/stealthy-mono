package top.secret.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import top.secret.exceptions.UserError;
import top.secret.pojo.User;
import top.secret.pojo.config.Collections;
import top.secret.pojo.schemas.UserRequest;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private PasswordEncoder passwordEncoder;
    private MongoTemplate template;

    @Autowired
    public UserService(
            @NonNull PasswordEncoder passwordEncoder,
            @NonNull MongoTemplate mongoTemplate
    ) {
        setPasswordEncoder(passwordEncoder);
        setTemplate(mongoTemplate);
    }

    public void setPasswordEncoder(@NonNull PasswordEncoder
                                           passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setTemplate(@NonNull MongoTemplate template) {
        this.template = template;
    }

    @Override
    public UserDetails loadUserByUsername(String s)
            throws UsernameNotFoundException {
        try {
            return getUserByUsername(s);
        } catch (UserError e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    public User getUserByUsername(String username) throws UserError {
        log.debug("Retrieving user '" + username + "'");

        Query query = new Query();
        query.addCriteria(Criteria.where("username").is(username));
        User user = template.findOne(
                query, User.class, Collections.USERS.getName()
        );
        if (user == null) {
            throw new UserError("User '" + username + "' not found");
        }
        return user;
    }

    public void getUserByCredentials(@NonNull UserRequest request) throws UserError {
        User user = getUserByUsername(request.getUsername());
        if(!passwordEncoder.matches(request.getPassword(),
                user.getPassword())) {
            throw new UserError("Invalid password");
        }
    }

    public boolean checkUserExists(String username) {
        log.debug("Checking user '" + username + "' exists");

        Query query = new Query();
        query.fields().exclude("password");
        query.addCriteria(Criteria.where("username").is(username));
        return template.exists(query, Collections.USERS.getName());
    }

    public void addUser(@NonNull UserRequest request) throws UserError {
        log.debug("Adding new user '" + request.getUsername());

        if (checkUserExists(request.getUsername())) {
            throw new UserError(
                    "User '" + request.getUsername() + "' already exist"
            );
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        template.save(user, Collections.USERS.getName());
    }
}
