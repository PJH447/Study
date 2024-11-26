package com.demo.lucky_platform.web_nosql.sampleData.service;

import com.demo.lucky_platform.web_nosql.sampleData.dto.CreateSampleDataReq;
import com.demo.lucky_platform.web_nosql.sampleData.entity.SampleDataInfo;

public interface SampleDataService {

    void save(CreateSampleDataReq req);

    SampleDataInfo findById(String id);

}
