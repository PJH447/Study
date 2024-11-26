package com.demo.lucky_platform.web_nosql.sampleData.repository;

import com.demo.lucky_platform.web_nosql.sampleData.entity.SampleDataInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SampleDataRepository extends MongoRepository<SampleDataInfo, String> {

    @Query("{name: {$regex: ?0}}")
    List<SampleDataInfo> findByName(String name);

    @Query("{count: {$gt: ?0}}")
    List<SampleDataInfo> findTest(Integer count);



}
