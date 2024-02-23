package com.rws.lt.lc.mtsampleapp.domain;

import lombok.ToString;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;

@ToString
public abstract class PersistedDomain {

    @Id
    private String id;

    @Version
    @Field("version")
    private Long version;

    @CreatedDate
    @Field("creationDate")
    private DateTime creationDate;

    @LastModifiedDate
    @Field("lastModifiedDate")
    private DateTime lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
