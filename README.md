# MyRetail-Rest-Service

This Project contains set of APIs to read/update Product information.

  
## Getting Started

  Once application is up & running (refer. below steps of Installation/Build & Running), API can be validated either with Swagger or using curl commnds.
  
  Swagger :
   
   ```
      http://localhost:8080/swagger-ui.html
      http://192.168.1.103:8080/swagger-ui.html
   ```

  CURL GET API:     
       
   ```
     curl -X GET --header 'Accept: application/json' --header 'X-CLIENT-ID: XXXX' --header 'X-CLIENT-SECRET: 9x44bun4fc9b96ugswhhwfe4p6mb3qe6' 'http://localhost:8080/products/13860428'
   ```
   CURL PUT API:
         
    curl -X PUT --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-CLIENT-ID: XXXX' --header 'X-CLIENT-SECRET: 9x44bun4fc9b96ugswhhwfe4p6mb3qe6' -d '{"current_price": { "currency_code": "USD","value": 100.11 },"id": 13860428,"name": "The Big Lebowski (Blu-ray)"}' 'http://localhost:8080/products/13860428'
  
### Prerequisites

 Running application locally needs installation of Java,Maven,Cassandra. Please follow installation,build processes documented below to make application up & running in local.
```
Java
Maven
Cassandra
```
### Installation

Once Java,Cassandra are downloaded & installed, follow below commands to create keyspace/table in Cassandra

Running Cassandra:
```
 sh {cassandra_home}/bin/cassandra -f
```
Creating keyspace/table:

```
sh {cassandra_home}/bin/cqlsh
cqlsh> SOURCE '/{myretail-rest-service_code_directory}/setup.cql'
```

## Build & Running

   Once in project directory ```mvn clean install spring-boot:run``` will bring application up & running. But for a more traditional way below 2 steps need to be followed
         

  1. Building artifact :
     ```
      mvn clean install
     ```
  2. Running :
      ```
       java -jar  target/myretail-rest-service-1.0-SNAPSHOT.jar
      ```
  
 
## Technology stack

* [Groovy](http://groovy-lang.org/) - Programming Language
* [SpringBoot](https://projects.spring.io/spring-boot/) - The web framework
* [Spock](http://spockframework.org/) - Unit/Integration testing framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [Cassandra](http://cassandra.apache.org/) - Used for data storage
* [Ehcache](http://www.ehcache.org/) - Level 1 Caching layer
* [Swagger](https://swagger.io/) - API documentation

## Author

* **Ganesh Kandisa** - *Initial work*(https://github.com/TheGanesh)

