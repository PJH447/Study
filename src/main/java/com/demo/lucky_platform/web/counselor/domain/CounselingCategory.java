package com.demo.lucky_platform.web.counselor.domain;

import lombok.Getter;

@Getter
public enum CounselingCategory {

    CATEGORY_EXAMPLE("categoryString"),
    ;

    private String categoryName;

    CounselingCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
