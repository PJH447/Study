package com.demo.lucky_platform.web.counselor.domain;

import lombok.Getter;

@Getter
public enum Site {

    SITE_EXAMPLE("siteName"),

    ;

    private String siteName;

    Site(String siteName) {
        this.siteName = siteName;
    }
}
