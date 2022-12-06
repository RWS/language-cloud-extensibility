package com.rws.lt.lc.mtsampleaddon.web;

import com.rws.lt.lc.mtsampleaddon.service.TranslateService;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslateRequest;
import com.rws.lt.lc.mtsampleaddon.transfer.TranslateResponse;
import com.rws.lt.lc.mtsampleaddon.util.RequestLocalContext;
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
