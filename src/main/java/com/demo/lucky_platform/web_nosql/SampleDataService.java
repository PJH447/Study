package com.demo.lucky_platform.web_nosql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleDataService {

    private final SampleDataRepository sampleDataRepository;

    public void save() {
        SampleDateInfo sampleDateInfo = SampleDateInfo.builder()
                                                      .name("JHH")
                                                      .email("test@email.com")
                                                      .address("seoul")
                                                      .build();
        SampleDateInfo save = sampleDataRepository.save(sampleDateInfo);
        System.out.println("save.toString() = " + save.toString());
    }

}
