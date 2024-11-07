package com.demo.lucky_platform.web_nosql;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        System.out.println("save.toString() = " + save.toString());
    }

    public void find(String id) {
//        SampleDateInfo sample = sampleDataRepository.findById(id)
//                                                    .orElseThrow(RuntimeException::new);
        List<SampleDateInfo> jhh = sampleDataRepository.findByName("H");

        List<SampleDateInfo> sampleDateInfos = sampleDataRepository.saveAll(jhh);
//
        for (SampleDateInfo sampleDateInfo : jhh) {
            System.out.println("sampleDateInfo = " + sampleDateInfo);
        }
//        System.out.println("save.toString() = " + sample);

//        List<SampleDateInfo> test = sampleDataRepository.findTest(4);
//        for (SampleDateInfo sampleDateInfo : test) {
//            System.out.println("sampleDateInfo = " + sampleDateInfo);
//
//        }

    }

}
