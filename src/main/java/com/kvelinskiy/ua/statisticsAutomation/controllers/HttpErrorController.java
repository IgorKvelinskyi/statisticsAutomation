package com.kvelinskiy.ua.statisticsAutomation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Igor Kvelinskyi (igorkvjava@gmail.com)
 */
@Controller
public class HttpErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;

    public HttpErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /*private final MessageSource messageSource;

        @Autowired
        public HttpErrorController(MessageSource messageSource) {
            this.messageSource = messageSource;
        }
    */
    @RequestMapping("/error")
    public String handleError(
            Locale locale,
            Model model,
            HttpServletRequest request,
            Exception exceptionSpring,
            WebRequest webRequest
    ) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        final Throwable errorStack = errorAttributes.getError(webRequest);
        String stack = String.valueOf(errorStack);
        model.addAttribute("stack", stack);
        model.addAttribute("errorStack", errorStack);
        model.addAttribute("exceptionSpring", exceptionSpring);
        if (status != null) {

            int statusCode = Integer.valueOf(status.toString());

            Map<String, String> metaData = new HashMap<>();
            model.addAttribute("msg", statusCode);


            // 403
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("msg", statusCode);
                // do somthing
            }

            // 404
            else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("msg", statusCode);
                // do somthing
            }

            // 405
            else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("msg", statusCode);
                // do somthing
            }

            // 500
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("msg", statusCode);
                // do somthing
            }

        }
        return "/error";
    }

    public String getErrorPath() {
        return "/error";
    }
}
