package com.rws.lt.lc.mtsampleapp.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "registration")
@AllArgsConstructor
public class RegistrationStatus {

    @Id
    private final String registered;

}
