package com.rws.lt.lc.mtsampleapp.service;

import com.rws.lt.lc.mtsampleapp.transfer.TranslationEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation for {@link TranslateService} that provides mock translations for the content received from Language Cloud
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "mockExtension", name = "enabled", havingValue = "true")
public class MockTranslateService implements TranslateService {

    /**
     * Translates the received contents on the translation request
     * @param accountId the accountId from the context
     * @param engineId the translation engine id(should be specific to the source and target language pairs)
     * @param contents the contents coming from Language Cloud
     * @return a list that contains the translated contents
     * @throws XMLStreamException
     */
    @Override
    public List<String> translate(String accountId, String engineId, List<String> contents) throws XMLStreamException {
        LOGGER.debug("translate with mock service >> accountId: {} engineId: {}", accountId, engineId);
        TranslationEngine.toTranslationEngine(engineId);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        List<String> translations = new ArrayList<>();
        for (String content : contents) {
            translations.add(translateHtml(xmlInputFactory, content));
        }

        return translations;
    }

    /**
     * Translates the contents by reversing the text.
     * @param xmlInputFactory the xml input factory used to parse the html input
     * @param html the html content
     * @return the translated content
     * @throws XMLStreamException
     */
    private String translateHtml(XMLInputFactory xmlInputFactory, String html) throws XMLStreamException {
        XMLEventReader xmlEventReader;
        String translated = html;

        xmlEventReader = xmlInputFactory.createXMLEventReader(new ByteArrayInputStream(html.getBytes()), StandardCharsets.UTF_8.name());
        String text = "";
        while (xmlEventReader.hasNext()) {
            XMLEvent e = xmlEventReader.nextEvent();
            if (e instanceof Characters) {
                String currentText = HtmlUtils.htmlEscape(((Characters) e).getData());
                text += currentText;
            } else {
                if (StringUtils.isNotBlank(text)) {
                    StringBuilder textBuilder = new StringBuilder(text);
                    String translation = textBuilder.reverse().toString();
                    translated = translated.replace(text, translation);
                    text = "";
                }
            }
        }
        return translated;
    }
}
