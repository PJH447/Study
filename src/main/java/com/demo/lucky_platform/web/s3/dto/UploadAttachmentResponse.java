package com.demo.lucky_platform.web.s3.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UploadAttachmentResponse {

    private String fileName;
    private long fileSize;
    private String fileContentType;
    private String link;

}