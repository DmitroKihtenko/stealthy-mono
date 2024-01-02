package top.secret.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;
import top.secret.exceptions.UserError;
import top.secret.pojo.config.InstitutionConfig;

import java.util.AbstractCollection;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public abstract class BaseController {
    private InstitutionConfig institutionInfo;

    public BaseController(InstitutionConfig institutionInfo) {
        this.institutionInfo = institutionInfo;
    }

    protected void addInstitutionInfo(ModelAndView modelAndView) {
        modelAndView.addObject("institutionInfo",
                institutionInfo);
    }

    protected List<String> getErrors(Errors object) {
        List<String> list = new LinkedList<>();
        for (ObjectError error : object.getAllErrors()) {
            list.add(error.getDefaultMessage());
        }
        return list;
    }

    protected void getErrors(
            Errors object, AbstractCollection<String> errors
    ) {
        for (ObjectError error : object.getAllErrors()) {
            errors.add(error.getDefaultMessage());
        }
    }

    protected List<String> getErrors(Exception e) {
        List<String> list = new LinkedList<>();
        String message = "Unknown user error";
        if (e.getMessage() != null) {
            message = e.getMessage();
        }
        list.add(message);
        return list;
    }

    @NonNull
    protected String getCurrentUsername() throws UserError {
        String name = SecurityContextHolder.getContext().
                getAuthentication().getName();
        if(name == null) {
            throw new UserError("You are not authorized");
        }
        return name;
    }
}
