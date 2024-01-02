package top.secret.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import top.secret.exceptions.UserError;
import top.secret.pojo.User;
import top.secret.pojo.config.InstitutionConfig;
import top.secret.pojo.schemas.UserRequest;
import top.secret.service.UserService;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Slf4j
@Controller
public class UsersController extends BaseController {
    private UserService userService;

    @Autowired
    public UsersController(InstitutionConfig institutionInfo) {
        super(institutionInfo);
    }

    @Autowired
    public void setUserService(@NonNull UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/sign-up")
    public ModelAndView signUpPage(ModelAndView mav) {
        log.info("Requested sign-up page");

        mav.setViewName("sign-up");
        addInstitutionInfo(mav);
        return mav;
    }

    @PostMapping(path = "/sign-up")
    public ModelAndView addUser(
            HttpServletRequest req,
            @Valid @ModelAttribute() UserRequest request,
            BindingResult bindingResult,
            ModelAndView mav
    ) {
        log.info("Requested creating user action");

        mav = signUpPage(mav);
        mav.addObject("request", request);
        try {
            if (!mav.getModelMap().containsAttribute("error")) {
                if (bindingResult.hasErrors()) {
                    mav.addObject(
                            "error",
                            getErrors(bindingResult)
                    );
                } else {
                    userService.addUser(request);
                    authenticateUser(req, request);
                    mav.setViewName("redirect:/space");
                }
            }
        } catch (UserError e) {
            mav.addObject(
                    "error",
                    getErrors(e));
        }

        log.info("User created");

        return mav;
    }

    @GetMapping(path = "/sign-in")
    public CompletableFuture<ModelAndView> signInPage(ModelAndView mav) {
        log.info("Requested sign-in page");

        mav.setViewName("sign-in");
        addInstitutionInfo(mav);
        return CompletableFuture.completedFuture(mav);
    }

    @PostMapping(path = "/sign-in")
    public ModelAndView signInUser(
            HttpServletRequest req,
            @Valid @ModelAttribute("request") UserRequest request,
            BindingResult bindingResult,
            ModelAndView mav
    ) {
        log.info("Requested user sign-in action");

        signInPage(mav);
        mav.addObject("request", request);
        if (!mav.getModelMap().containsAttribute("error")) {
            if (bindingResult.hasErrors()) {
                mav.addObject(
                        "error",
                        getErrors(bindingResult)
                );
            } else {
                try {
                    userService.getUserByCredentials(request);
                    authenticateUser(req, request);
                    mav.setViewName("redirect:/space");
                } catch (UserError e) {
                    mav.addObject(
                            "error",
                            getErrors(e)
                    );
                }
            }
        }
        return mav;
    }

    protected void authenticateUser(
            HttpServletRequest request, UserRequest userRequest
    ) {
        UsernamePasswordAuthenticationToken auth
                = new UsernamePasswordAuthenticationToken(
                new User(userRequest.getUsername(), null),
                null, new ArrayList<>()
        );

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        log.info("User successfully authenticated");
    }
}
