MySQL
- 
```
docker run --name local-mysql -e MYSQL_ROOT_PASSWORD=1234 -d -p 3306:3306 mysql:latest
```

Redis
- 
```
docker run -d -p 6379:6379 --name redis-test-container redis:latest --requirepass "pass"
```

MongoDB
-
```
docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
```

kafka
-
- run docker-compose
```
docker-compose -f docker-compose.yml up -d
```

- create topic
```
docker-compose exec kafka kafka-topics --create --topic my-topic --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1
```

- describe topic
```
docker-compose exec kafka kafka-topics --describe --topic my-topic --bootstrap-server kafka:9092
```
- consumer
```
docker-compose exec kafka bash
kafka-console-consumer --topic my-topic --bootstrap-server kafka:9092
```

- producer
```
docker-compose exec kafka bash
kafka-console-producer --topic my-topic --broker-list kafka:9092
```

