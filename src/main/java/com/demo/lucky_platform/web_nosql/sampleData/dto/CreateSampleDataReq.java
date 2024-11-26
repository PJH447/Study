package com.demo.lucky_platform.web_nosql.sampleData.dto;

public record CreateSampleDataReq(String id,
                                  String name,
                                  String email,
                                  String address,
                                  Integer count
) {
}
