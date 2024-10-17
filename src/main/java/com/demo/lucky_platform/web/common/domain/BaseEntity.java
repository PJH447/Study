package com.demo.lucky_platform.web.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 12345L;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "datetime", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", columnDefinition = "bigint(20)", updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "last_updated_at", columnDefinition = "datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdatedAt;

    @LastModifiedBy
    @Column(name = "last_modified_by", columnDefinition = "bigint(20)")
    private Long lastModifiedBy;

    @Column(name = "enabled", columnDefinition = "bit(1)")
    private Boolean enabled = true;

    public void disabled() {
        this.enabled = false;
    }
}
