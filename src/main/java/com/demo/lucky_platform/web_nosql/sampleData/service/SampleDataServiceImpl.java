package com.demo.lucky_platform.web_nosql.sampleData.service;

import com.demo.lucky_platform.web_nosql.sampleData.dto.CreateSampleDataReq;
import com.demo.lucky_platform.web_nosql.sampleData.entity.SampleDataInfo;
import com.demo.lucky_platform.web_nosql.sampleData.repository.SampleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class SampleDataServiceImpl implements SampleDataService {

    private final SampleDataRepository sampleDataRepository;

    @Override
    public void save(final CreateSampleDataReq req) {
        SampleDataInfo sampleDateInfo = SampleDataInfo.builder()
                                                      .name(req.name())
                                                      .email(req.email())
                                                      .address(req.address())
                                                      .count(req.count())
                                                      .build();
        sampleDataRepository.save(sampleDateInfo);
    }

    @Override
    public SampleDataInfo findById(final String id) {
        return sampleDataRepository.findById(id)
                                   .orElseThrow(RuntimeException::new);
    }

}
