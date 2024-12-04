package com.rws.lt.lc.mtsampleapp.web;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rws.lt.lc.mtsampleapp.security.GenericAuthorization;
import com.rws.lt.lc.mtsampleapp.service.AppMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Slf4j
public class MetadataController {

    @Autowired
    private AppMetadataService metadataService;


    @RequestMapping(value = "/descriptor", method = RequestMethod.GET)
    @GenericAuthorization
    public ObjectNode getDescriptor() {
        LOGGER.info("getDescriptor >>");
        return metadataService.getDescriptor();
    }

}
