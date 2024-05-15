package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.security.GenericAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/v1")
@Slf4j
public class DocumentationController {

    private final String documentationRedirect;

    public DocumentationController(@Value("${documentation.url}") String documentationUrl) {
        documentationRedirect = "redirect:" + documentationUrl;
    }

    @RequestMapping(value = "/documentation", method = RequestMethod.GET)
    @GenericAuthorization
    public ModelAndView getDocumentation() {
        LOGGER.info("getDocumentation >>");
        return new ModelAndView(documentationRedirect);
    }
}
