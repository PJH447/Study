package com.demo.lucky_platform.web_nosql;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleDataRepository extends MongoRepository<SampleDateInfo, String> {
}
