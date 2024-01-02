package top.secret.pojo.config;

import lombok.Getter;

@Getter
public enum Collections {
    USERS("users"),
    FILES("files"),
    FILES_METADATA("files_metadata");

    private final String name;

    Collections(String name) {
        this.name = name;
    }
}
