docker run --name local-mysql -e MYSQL_ROOT_PASSWORD=1234 -d -p 3306:3306 mysql:latest
docker run -d -p 6379:6379 --name redis-test-container redis:latest --requirepass "pass"
docker run --name mongodb -p 27017:27017 -d mongodb/mongodb-community-server:latest
