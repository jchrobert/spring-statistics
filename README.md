REST API Statistics
===
The main use case of the API is to recieve transactions and calculate realtime statistic based on the transactions from the last 60 seconds.



**Run Project**

```shell
mvn spring-boot:run
``` 


**Package & execute tests**

```shell
mvn clean install
``` 
The server is running on 'http://localhost:8080/'

## Endpoints


* /transaction : This endpoint will be called every time a new transaction happened.
* /statistics: This endpoint returns the statistic based on the transactions which happened in the last 60 seconds.


## Features

* Spring Boot / Maven / Lombok / Junit & Mockito 
* No database and in-memory database neither
* Endpoints are non-blocking and thread-Safe with concurrent requests
* Performances: The endpoints execute in constant time and space O(1)
* As much as possible, focused on simple, readable and understandable code.

## Solutions

#### Endpoints 
  * /transaction: The endpoint simply adds the transaction in a ConcurrentLinkedQueue#add, which is a O(1) cost. Then an event is triggered to recalculate the statistics asynchronously.
  * /statistics: The statistics are stored as a cache and thus always available. The simple call to get the cache entry is also constant O(1) and saves us the cost of the calculation.

#### Implementation 

The solution used to get the statistics in real-time, is to store the transaction in a queue, and save the calculated statistics as a cache entry.
1. Events: When a new transaction is posted, triggers the recalculation of the statistics.
2. A scheduled task runs the recalculation and update the statistics every seconds for the new range of less than 60 seconds transactions
3. A second scheduled task runs every 2 minutes to clean the transaction queue and remove the expired transactions. The costs of filtering the queue is less important if the queue get not too large.



**Alternative Solutions**

One alternative solution could be to have 2 applications communicating with a real messaging system such as JMS or **RabbitMQ**.

Another solution would be to implement the application with **Spring Webflux** and make the app reactive. For example open a stream of statistics to offer to a frontend application.

**Tests**
* Tests with running server
* Tests MVC Layer (Controller)
* Unit testing

**TODO**

* Proper and complete API documentation with Spring REST Docs and/or OpenAPI (swagger-springfox)
* Multi-threaded stress tests with high load of transactions and read of statistics.
* Dockerization of the project.

