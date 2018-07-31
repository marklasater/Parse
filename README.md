# Assumptions
* This program is defaulted to use an in memory database. 
    * This decision was made for ease of testing and ease of running the application without knowing details about db details 
    * If you want to establish a different database connection see the `Custom DB` instructions below
* Log lines are formatted with `|` delimiter and are in the format:
```
2017-01-01 22:00:09.116|192.168.147.126|"GET / HTTP/1.1"|200|"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0"
```

# Run Application

### In Memory Database
* Go to solution
```
cd Parser
```
* Build solution
```
mvn clean install
```
* Run solution
```
java -jar target/parser.jar --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
```


### Custom DB
* Open the following file
```
Parser/src/main/resources/application.properties
```
* Uncomment the following lines and modify DB credentials
```
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver
#spring.datasource.url=jdbc:mysql://localhost:3306/parser?serverTimezone=UTC
#spring.datasource.username=root
#spring.datasource.password=pass
```
* Go to solution
```
cd Parser
```
* Build solution
```
mvn clean install
```
* Run solution
```
java -jar target/parser.jar --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
```