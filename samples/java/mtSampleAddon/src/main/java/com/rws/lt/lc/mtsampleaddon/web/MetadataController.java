package com.rws.lt.lc.mtsampleaddon.web;

import com.rws.lt.lc.mtsampleaddon.security.GenericAuthorization;
import com.rws.lt.lc.mtsampleaddon.service.AddonMetadataService;
import com.rws.lt.lc.mtsampleaddon.transfer.Descriptor;
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
    private AddonMetadataService metadataService;


    @RequestMapping(value = "/descriptor", method = RequestMethod.GET)
    @GenericAuthorization
    public Descriptor getDescriptor() {
        LOGGER.info("getDescriptor >>");
        return metadataService.getDescriptor();
    }

}
