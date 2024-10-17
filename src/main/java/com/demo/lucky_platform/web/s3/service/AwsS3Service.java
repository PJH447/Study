package com.demo.lucky_platform.web.s3.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.demo.lucky_platform.web.s3.dto.UploadAttachmentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AwsS3Service {

    @Value("${app.s3.cloud.end-point}")
    private String endPoint;

    @Value("${app.s3.cloud.access-key}")
    private String accessKey;

    @Value("${app.s3.cloud.secret-key}")
    private String secretKey;

    @Value("${app.s3.cloud.region-name}")
    private String regionName;

    @Value("${app.s3.cloud.bucket-name}")
    private String bucketName;

    public UploadAttachmentResponse uploadFile(String filePath, String fileName, MultipartFile multipartFile) {
        return uploadObject(filePath, fileName, multipartFile);
    }

    public UploadAttachmentResponse uploadFile(String filePath, MultipartFile multipartFile) {
        String sourceFileName = multipartFile.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;

        return uploadObject(filePath, destinationFileName, multipartFile);
    }

    public List<UploadAttachmentResponse> uploadFile(String filePath, String fileName,
                                                     List<MultipartFile> multipartFileList) {
        List<UploadAttachmentResponse> list = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            UploadAttachmentResponse result = uploadObject(filePath, fileName, multipartFile);
            list.add(result);
        }

        return list;
    }

    private UploadAttachmentResponse uploadObject(String filePath, String objKeyName, MultipartFile multipartFile) {
        if (!ObjectUtils.isEmpty(filePath)) {
            filePath = filePath.replaceAll("^/", "");
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                                                 .withEndpointConfiguration(
                                                         new AwsClientBuilder.EndpointConfiguration(endPoint,
                                                                 regionName))
                                                 .withCredentials(new AWSStaticCredentialsProvider(
                                                         new BasicAWSCredentials(accessKey, secretKey)))
                                                 .build();

        UploadAttachmentResponse result = new UploadAttachmentResponse();

        try {
            InputStream inputStream = multipartFile.getInputStream();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setCacheControl("max-age=1296000");
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            PutObjectRequest request = new PutObjectRequest(bucketName, filePath + objKeyName,
                    inputStream, objectMetadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(request);

            result.setLink(filePath + objKeyName);
            result.setFileContentType(multipartFile.getContentType());
            result.setFileName(objKeyName);
            result.setFileSize(objectMetadata.getContentLength());

        } catch (IOException e) {
            throw new AmazonS3Exception(e.getMessage(), e);
        }

        return result;
    }

    public UploadAttachmentResponse uploadFileUsingResize(String filePath, int width, int height, String fileName,
                                                          MultipartFile multipartFile) {
        return uploadObjectUsingResize(filePath, width, height, fileName, multipartFile);
    }

    public UploadAttachmentResponse uploadFileUsingResize(String filePath, int width, int height,
                                                          MultipartFile multipartFile) {

        String sourceFileName = multipartFile.getOriginalFilename();
        String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();
        String destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;

        return uploadObjectUsingResize(filePath, width, height, destinationFileName, multipartFile);
    }

    public List<UploadAttachmentResponse> uploadFileUsingResize(String filePath, int width, int height, String fileName,
                                                                List<MultipartFile> multipartFileList) {
        List<UploadAttachmentResponse> list = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            UploadAttachmentResponse result = uploadObjectUsingResize(filePath, width, height, fileName, multipartFile);
            list.add(result);
        }

        return list;
    }

    private UploadAttachmentResponse uploadObjectUsingResize(String filePath, int width, int height, String objKeyName,
                                                             MultipartFile multipartFile) {

        if (!ObjectUtils.isEmpty(filePath)) {
            filePath = filePath.replaceAll("^/", "");
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                                                 .withEndpointConfiguration(
                                                         new AwsClientBuilder.EndpointConfiguration(endPoint,
                                                                 regionName))
                                                 .withCredentials(new AWSStaticCredentialsProvider(
                                                         new BasicAWSCredentials(accessKey, secretKey)))
                                                 .build();

        UploadAttachmentResponse result = new UploadAttachmentResponse();
        Image image = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        boolean usingResinze = false;
        String contentType = multipartFile.getContentType();
        try {
            inputStream = multipartFile.getInputStream();

            image = ImageIO.read(inputStream);

//            if (image.getWidth(null) > width) {
//                File folder = new File(thumbnailFilePath);
//                if (!folder.exists()) {
//                    folder.mkdir();
//                }
//
//                outputStream = new FileOutputStream(new File(sourceFileName));
//                outputStream.write(multipartFile.getBytes());
//
//                Tinify.setKey(tinyfyKey);
//
////                AnytarotConfig compressionCnt = anytarotConfigService.findByKey(AnytarotConfig.AnytarotConfigKey.CONFIG_TINYFY_MONTH_COUNT);
//
//                int compressionsThisMonth = Tinify.compressionCount();
//
//                if (compressionsThisMonth < Integer.parseInt(compressionCnt.getValue())) {
//                    Source source = Tinify.fromFile(sourceFileName);
//
//                    Options options = new Options()
//                            .with("method", "fit")
//                            .with("width", width)
//                            .with("height", height);
//                    source.resize(options).toFile(thumnailFileName);
//                } else {
//                    Thumbnails.of(sourceFileName)
//                              .size(width, height)
//                              .toFile(new File(thumnailFileName));
//                }
//
//                inputStream.close();
//                inputStream = new FileInputStream(new File(thumnailFileName));
//
//                usingResinze = true;
//            }

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setCacheControl("max-age=1296000");
            objectMetadata.setContentLength(inputStream.available());

            PutObjectRequest request = new PutObjectRequest(bucketName, filePath + objKeyName,
                    inputStream, objectMetadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            s3.putObject(request);

            result.setLink(filePath + objKeyName);
            result.setFileContentType(contentType);
            result.setFileName(objKeyName);
            result.setFileSize(objectMetadata.getContentLength());

//            if (usingResinze) {
//                outputStream.close();
//                // 기존 input stream 종료
//                outputStream = null;
//
//                File file = new File(sourceFileName);
//
//                if (file.exists()) {
//                    file.delete();
//                }
//
//                file = new File(thumnailFileName);
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
        } catch (IOException e) {
            throw new AmazonS3Exception(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return result;
    }


    public void deleteObject(String filePath, String objKeyName) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                                                 .withEndpointConfiguration(
                                                         new AwsClientBuilder.EndpointConfiguration(endPoint,
                                                                 regionName))
                                                 .withCredentials(new AWSStaticCredentialsProvider(
                                                         new BasicAWSCredentials(accessKey, secretKey)))
                                                 .build();

        s3.deleteObject(bucketName, filePath + objKeyName);

    }
}
