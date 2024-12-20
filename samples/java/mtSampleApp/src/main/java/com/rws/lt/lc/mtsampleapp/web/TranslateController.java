package com.rws.lt.lc.mtsampleapp.web;

import com.rws.lt.lc.extensibility.security.util.RequestLocalContext;
import com.rws.lt.lc.mtsampleapp.service.TranslateService;
import com.rws.lt.lc.mtsampleapp.transfer.TranslateRequest;
import com.rws.lt.lc.mtsampleapp.transfer.TranslateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.xml.stream.XMLStreamException;
import java.util.List;

@RestController
@RequestMapping("/v1/translate")
@Slf4j
public class TranslateController {

    @Autowired
    private TranslateService translateService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TranslateResponse translate(@RequestBody @Valid TranslateRequest request) throws XMLStreamException {
        // Endpoint used to receive and translate the contents from LC
        LOGGER.info("translate >> text {}", request);

        List<String> translate = translateService.translate(RequestLocalContext.getActiveAccountId(), request.getEngineId(), request.getContents());

        return new TranslateResponse(translate);
    }
}
