package com.rws.lt.lc.mtsampleapp.service;

import javax.xml.stream.XMLStreamException;
import java.util.List;

public interface TranslateService {

    List<String> translate(String accountId, String engineId, List<String> htmls) throws XMLStreamException;
}
