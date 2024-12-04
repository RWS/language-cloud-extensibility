package com.rws.lt.lc.mtsampleapp.web;

import com.rws.lt.lc.mtsampleapp.security.GenericAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/v1")
@Slf4j
public class PrivacyPolicyController {
    @RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
    @GenericAuthorization
    public ModelAndView getPrivacyPolicy() {
        LOGGER.info("getPrivacyPolicy >>");
        return new ModelAndView("privacyPolicy.html");
    }
}
