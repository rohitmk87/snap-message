# snap-message
UACF Assignment

Components:
1. Sprint Boot application. 
2. Design patterns: MVC, DAO layer.
2. Cassandra as storage.
3. Hosted 3 services as per requirement.

As part of Microservice architecture that I am following to implement this assignment, I am considering the above stack. This is horizontally scalable service which can be deployed in any cloud environment. 
Stack Example: AWS cloud, Docker containers, Cassandra, Config server, Jenkins Job to deploy, Java 8, Spring Boot. Netflix Open Source: Zuul Edge server, Eureka discovery.

Storage:
I have used Cassandra as the storage system. Used 2 tables. 1st table - Messages is to store id (primary key), texts, corresponding username, ttl, HOT/COLD storage type and timestamp. The 2nd table - Message_by_user is to hold username and the corresponding text ids as list.

Data Model:

CREATE TABLE jcp_account.messages (
    id text PRIMARY KEY,
    storage_type text,
    text text,
    timestamp timestamp,
    ttl text,
    username text
)

CREATE TABLE jcp_account.message_by_user (
    username text PRIMARY KEY,
    id list<text>
)

Design and Working:
1. POST /chat - New Entry: I will insert in message_by_user table the username and 'id as list'. Another corresponding entry will be done in messages table. In this I will store all the relevant fields as mentioned above.
New Entry for same username: I will update the id list entry in message_by_user table and make insert new entry into messages table for the above mentioned fields. 
All the new text entries above will be stored as HOT with corresponding ttl.
2. GET /chats/{username} - I will first lookup messages table for the username. Get list of ids from here. Return all the entries from messages table for the corresponding ids that are in HOT state(not expired). While I return these entries, I will mark those entries as COLD. So the next you rerun the api call, you will not get those entries in response as they have already marked as expired(COLD).
3. GET /chat/{id} - In this case, I will directly lookup meesages table against the incoming id. I will return username, text and expiration_date in response irrespective of it Storage state(HOT/COLD). While I return this entry, if it is HOT, I will will mark this entry as COLD if the ttl has expired. This way I will be pushing the texts from HOT to COLD state in real time whenever there is a GET call (as in point$ 2 and 3) whenever the necessary conditions are met per requirement.


Below are service sample request response:
1. POST http://localhost:8090/chat
Request

{
	"username" : "rkamble",
	"text" : "This is my id6",
	"timeout" : 60
}

Response: Status Code: 201 Created

{
    "id": "wM8Aa4LS4CpGebmJ9vyx"
}


2. GET http://localhost:8090/chats/rkamble

Response: Status code: 200 OK

[
    {
        "id": "wM8Aa4LS4CpGebmJ9vyx",
        "text": "This is my id6"
    }
]


3. GET http://localhost:8090/chat/wM8Aa4LS4CpGebmJ9vyx

Response: Status code: 200 OK

{
    "username": "rkamble",
    "text": "This is my id6",
    "expiration_date": "2018-04-25 19:34:27"
}

Main Code files:

SnapResource.java : This is the controller and is the entry point of the incoming requests for all above services. 
SnapService.java : This is the business logic layer.
MessageDaoImpl.java : This is where the application interact with Cassandra to perform various operations such as INsert, Update and Get.
application.yml: This is a configuration file. I have kept Keyspace and hostname as configurable entries. Everytime you update this file, there is a restart (no need to redeploy) of the application required.
Applciation Server: Spring Boot comes inbuilt with Tomcat server. The gradle build will generate the jar file and host it on the Tomcat server.

Development tools: Idea IntelliJ, Postman rest client, DataStax DSE (which comes with casssandra), Postman rest client. 

Limitation and Improvemnt: It is not best practice to update a resource during GET call as I am doing above to move COLD state. Solution for this is to setup batch jobs to check if the texts have met the ttl and expired, then move those texts to COLD state. 
Alternate Design Approach: Maintain 3 tables: 1) Meesage_by_user same as above. 2) Instead of Messages table(above) which includes both HOT and COLD storage entries, maintain 2 separate tables as HOT and COLD storage with same other fields. And have Cassandra Insert ttl only on HOT storage table. This way, entries from HOT storage table will expire(hence deleted) after the set ttl for each record is achieved and COLD storage will maintain all the records forever.

