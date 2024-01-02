package top.secret;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.*;

import java.io.IOException;
import java.util.LinkedList;


@Setter
@Getter
@Component
public class ExceptionFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private HandlerExceptionResolver exceptionResolver;

    private DispatcherServlet dispatcherServlet;

    @Autowired
    public ExceptionFilter(
            @Qualifier("handlerExceptionResolver")
            @NonNull HandlerExceptionResolver exceptionResolver,
            @NonNull DispatcherServlet dispatcherServlet
    ) {
        setExceptionResolver(exceptionResolver);
        setDispatcherServlet(dispatcherServlet);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.debug("Filter chain exception: " + e);
            LinkedList<String> errors = new LinkedList<>();
            errors.add(e.getMessage());
            request.setAttribute("error", errors);
            RequestDispatcher requestDispatcher =
                    getServletContext().getRequestDispatcher("/error-page");
            requestDispatcher.forward(request, response);
        }
    }
}
