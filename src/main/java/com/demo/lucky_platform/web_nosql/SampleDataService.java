package com.demo.lucky_platform.web_nosql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SampleDataService {

    private static Integer count = 1;
    private final SampleDataRepository sampleDataRepository;

    public void save() {
        SampleDateInfo sampleDateInfo = SampleDateInfo.builder()
                                                      .name("JHH")
                                                      .email("test@email.com")
                                                      .address("seoul")
                                                      .count(count)
                                                      .build();
        count += 1;

        SampleDateInfo save = sampleDataRepository.save(sampleDateInfo);
    }

    public SampleDateInfo find(String id) {
        return sampleDataRepository.findById(id)
                                   .orElseThrow(RuntimeException::new);

    }

}
