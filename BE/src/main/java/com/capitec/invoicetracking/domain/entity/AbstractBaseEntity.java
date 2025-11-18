package com.capitec.invoicetracking.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseEntity implements Serializable {

    @Column(name="created_at", nullable = false, updatable = false)
    @CreatedDate
    protected long createdAt;

    @JsonIgnore
    @Column(name="modified_at", nullable = false)
    @LastModifiedDate
    protected long modifiedAt;

    @JsonIgnore
    @CreatedBy
    @Column(name="created_by", nullable = false, updatable = false)
    protected String createdBy;

    @JsonIgnore
    @LastModifiedBy
    @Column(name="modified_by", nullable = false)
    protected String modifiedBy;
}

