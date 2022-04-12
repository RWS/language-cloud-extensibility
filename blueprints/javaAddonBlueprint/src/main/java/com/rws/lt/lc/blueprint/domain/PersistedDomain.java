package com.rws.lt.lc.blueprint.domain;

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
    @Field("v")
    private Long version;

    @CreatedDate
    @Field("ca")
    private DateTime creationDate;

    @LastModifiedDate
    @Field("lm")
    private DateTime lastModifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
