package top.secret.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import top.secret.exceptions.UserError;
import top.secret.pojo.config.InstitutionConfig;

@Slf4j
@ControllerAdvice
@Controller
public class ExceptionController extends BaseController {
    @Autowired
    public ExceptionController(InstitutionConfig institutionInfo) {
        super(institutionInfo);
    }

    @ExceptionHandler(UserError.class)
    public ModelAndView handleError(UserError e) {
        log.info("Handling error '" + e.getMessage() + "'");

        String message = "Unknown user error";
        if (e.getMessage() != null) {
            message = e.getMessage();
        }
        UserError newException = new UserError(message);
        ModelAndView mav = new ModelAndView("error-page");
        mav.addObject("error", getErrors(newException));
        return getErrorPage(mav);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnknownError(Exception e) {
        log.info("Handling error '" + e.getMessage() + "'");

        String message = "Unknown server error";
        if (e.getMessage() != null) {
            message = e.getMessage();
        }
        Exception newException = new Exception(message);
        ModelAndView mav = new ModelAndView("error-page");
        mav.addObject("error", getErrors(newException));
        return getErrorPage(mav);
    }

    @GetMapping(path = "/error-page")
    public ModelAndView getErrorPage(ModelAndView mav) {
        log.info("Requested error page");

        mav.setViewName("error-page");
        addInstitutionInfo(mav);
        return mav;
    }
}
