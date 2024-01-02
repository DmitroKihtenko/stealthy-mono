package top.secret.controller;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import top.secret.exceptions.UserError;
import top.secret.pojo.FileData;
import top.secret.pojo.FileMetadata;
import top.secret.pojo.config.InstitutionConfig;
import top.secret.pojo.schemas.FileIdParams;
import top.secret.service.FileMetadataService;
import top.secret.service.FileService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;

@Slf4j
@Controller
public class FilesController extends BaseController {
    private FileService fileService;
    private FileMetadataService fileMetadataService;
    private SmartValidator validator;

    @Autowired
    public FilesController(InstitutionConfig institutionInfo) {
        super(institutionInfo);
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setFileMetadataService(FileMetadataService fileMetadataService) {
        this.fileMetadataService = fileMetadataService;
    }

    @Autowired
    public void setValidator(SmartValidator validator) {
        this.validator = validator;
    }

    @GetMapping(path = "/space")
    public ModelAndView spacePage(
            ModelAndView mav,
            @Positive(message = "Page value should be positive number")
            @RequestParam(defaultValue = "1") int page,
            BindingResult bindingResult
    ) {
        log.info("Requested user space page");

        mav.setViewName("space");
        addInstitutionInfo(mav);
        mav.addObject(
                "filesConfig", fileMetadataService.getFilesConfig()
        );

        if (bindingResult != null && bindingResult.hasErrors()) {
            mav.addObject(
                    "error",
                    getErrors(bindingResult)
            );
        } else {
            try {
                String username = getCurrentUsername();
                mav.addObject(
                        "filesMetadata",
                        fileMetadataService.getFileMetadataList(username, page)
                );
            } catch (UserError e) {
                mav.addObject(
                        "error",
                        getErrors(e)
                );
            }
        }
        return mav;
    }

    @GetMapping(path = "/download")
    public Object downloadPage(
            @RequestParam(value = "file-id", defaultValue = "")
            String fileId,
            @RequestParam(value = "attachment", defaultValue = "false")
            String asAttachment,
            ModelAndView mav
    ) {
        log.info("Requested file downloading page");

        mav.setViewName("download");
        addInstitutionInfo(mav);

        if (Objects.equals(asAttachment, "true")) {
            FileIdParams fileIdParams = new FileIdParams();
            fileIdParams.setFileId(fileId);

            Errors errorsObj = validator.validateObject(fileIdParams);
            if (errorsObj.hasErrors()) {
                mav.addObject(
                        "error",
                        getErrors(errorsObj)
                );
                return mav;
            }

            try {
                FileData fileData = fileService.getFileById(fileId);
                FileMetadata metadata = fileMetadataService.
                        getFileMetadataById(fileId);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", metadata.getMimetype());

                String fileName = URLEncoder.encode(metadata.getName(),
                        StandardCharsets.UTF_8);
                fileName = fileName.replace("+", "%20");
                headers.add("Content-Disposition", "attachment; " +
                        "filename=\"" + fileName + "\"");

                headers.add("Content-Length",
                        Long.toString(metadata.getSize()));

                return new ResponseEntity<>(fileData.getData(), headers, HttpStatus.OK);
            } catch (UserError e) {
                mav.addObject("error", getErrors(e));
            }
        }
        return mav;
    }

    @PostMapping(path = "/space")
    public ModelAndView uploadFile(
            @RequestParam("file") MultipartFile file,
            ModelAndView mav
    ) {
        log.info("Requested adding file action");

        try {
            FileMetadata metadata = new FileMetadata();
            FileData fileData = new FileData();

            fileData.setIdentifier(fileMetadataService.generateFileId());
            try {
                fileData.setData(file.getBytes());
            } catch (IOException e) {
                throw new UserError(
                        "File reading error. File damaged"
                );
            }

            LocalDateTime now = LocalDateTime.now(ZoneId.of("Etc/UTC"));

            metadata.setIdentifier(fileData.getIdentifier());
            metadata.setName(file.getOriginalFilename());
            metadata.setUsername(SecurityContextHolder.getContext().
                    getAuthentication().getName());
            metadata.setSize(fileData.getData().length);
            metadata.setMimetype(file.getContentType());
            metadata.setCreation(now.toEpochSecond(ZoneOffset.UTC));

            metadata.setExpiration(now.plusMinutes(
                    fileMetadataService.getFilesConfig().
                            getExpirationMinutes()).
                    toEpochSecond(ZoneOffset.UTC));

            Errors errorsObj = validator.validateObject(metadata);
            HashSet<String> errors = new HashSet<>();
            if (errorsObj.hasErrors()) {
                getErrors(errorsObj, errors);
            }
            errorsObj = validator.validateObject(fileData);
            if (errorsObj.hasErrors()) {
                getErrors(errorsObj, errors);
            }
            if (errors.isEmpty()) {
                fileMetadataService.addFileMetadata(metadata);
                fileService.addFile(fileData);
            } else {
                mav.addObject("error", errors);
            }
        } catch (UserError e) {
            mav.addObject("error", getErrors(e));
        }
        return spacePage(mav, 1, null);
    }
}
