# Stealthy Application
Web application for secure sharing sensitive data:
- provides ability to sign-up and sign-in for users
- provides ability to upload, download user's files and view
details about uploaded files
- provides ability to share downloaded user's files
- provides service of automatic removing uploaded sensitive data
files

### Technologies
Build with:
- JDK 17
- Tomcat server
- Mongo DB 7.0.2
- Spring Boot 3.2.0
- Spring Security 3.2.0
- Spring Mongo DB 3.2.0

Works on HTTP web protocol.

### Requirements
Installed Docker and Docker-compose plugin

### API
You can view API details using Openapi standard mapping /swagger/index.html

### How to up and run
#### Configure application
1. Copy files: '.env.example' to '.env', 'application.yml.example' to
'src/main/resources/application.yml'.
2. Make changes you need in configuration files (details about configs can
be found in these files).
3. Add SSL cert files for your server: 'ssl/cert.pem', 'ssl/key.pem'.

#### Build docker images and start application
```bash
docker compose up
```

#### Check application is running
Go to your browser and open page 'https://localhost/download'

#### Stop application and remove containers after application use
```bash
docker compose down
```
