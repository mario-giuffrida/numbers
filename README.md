# Introduction

This application has been developed using Spring Boot. It uses JPA mySql database for storage and 
exposes HTTP endpoints to perform queries and uploads.

# Setting the Environment

### Database

Assuming that you have Docker installer, you can launch the mySql instance using the following command.

```
docker run -p 3306:3306 --name numbers-mysql -e MYSQL_ROOT_PASSWORD=numbers-password -d mysql:5.7
```

### Application
Once mySql DB is up and running, you can launch the application using the following commands from the project
root folder (`numbers`)

```
gradle clean build
cd build/libs
java -jar numbers-0.0.1-SNAPSHOT.jar
```

### Postman (optional) 
Optionally, you can also install the Postman collection you find in the root folder, named `African.postman_collection.json`.
This would help with running the assignment's tasks below. Otherwise, you can use `curl` commands (more details below).

# Running the Assignment

## Task 1 - Load the numbers
* Consume the provided file via any of the following means eg. upload / console call / API.
* Test each number and check for correctness, attempt to correct incorrectly formed numbers
and reject numbers that are invalid. (27831234567 is the correct format for this exercise).
* Store the results appropriately to DB / Temporary File - as per your discretion.

You can load the file using the `[NUMBERs] Load Number From File` endpoint in Postman. You just need to provide the `path` (absolute path) of the 
CSV file. Alternatively, you can use the following curl command, i.e.:

```
curl --location --request POST 'localhost:8080/numbers' \
--header 'Content-Type: application/json' \
--data-raw '{
    "path": "/Users/mgiuff/mycode/numbers/South_African_Mobile_Numbers.csv"
}'
```

Using the command above, all the numbers in the file would be checked for correctness, attempted to be corrected
or rejected if invalid. This would set the `state` of each number respectively to `ACCEPTABLE`, `CORRECTED` or `INCORRECT`.

You should see this in the logs, i.e.:

```
numbers loading started
[...]
2021-02-27 17:52:51.683 DEBUG 94543 --- [pool-1-thread-1] c.d.a.service.NumberProcessor            : number 27836826107 is CORRECTED, corrected [removed deleted postfix]
[...]
2021-02-27 17:52:51.700 DEBUG 94543 --- [pool-1-thread-1] c.d.a.service.NumberProcessor            : number 27781634994 is INCORRECT, corrected []
[...]
2021-02-27 17:52:52.238 DEBUG 94543 --- [pool-1-thread-1] c.d.a.service.NumberProcessor            : number 27837255270 is ACCEPTABLE, corrected []
[...]
```

## Task 2 - Display the results

Display results by the following means - divide the states as follows:
* Display acceptable numbers
* Corrected numbers + what was modified 
* Incorrect numbers.

You can display the number using the `localhost:8080/numbers/{state}` endpoint in Postman, called `[NUMBERs] Get Numbers By Status`. You just need to provide the
state of the orders you want to display. Possible values are `ACCEPTABLE`, `CORRECTED` or `INCORRECT`. Alternatively, you 
can use `curl` command. I.e. to display all the corrected number you can use the following command.

```
curl --location --request GET 'localhost:8080/numbers/CORRECTED'
```

And you will get a list of number which have been corrected + what was modified, i.e.:

```
[
    {
        "id": 266,
        "importedId": "103427420",
        "number": "27836826107",
        "corrections": [
            "removed deleted postfix"
        ]
    },
    {
        "id": 964,
        "importedId": "103427664",
        "number": "27836826107",
        "corrections": [
            "removed deleted postfix"
        ]
    }
]
```

## Task 3 - Single request

You can check for a single number to be checked using the `[NUMBERs] Chech Single Number` endpoint in Postman.
You need to provide the number to check as url param, i.e. localhost:8080/number/{number to check}.
Alternatively, you can use `curl` command, i.e.:

```
curl --location --request GET 'localhost:8080/number/27831234567_DELETED_fkdjhsfkjh'
```

The above command returns:

```
Number 27831234567, status CORRECTED, corrections [removed deleted postfix]
```