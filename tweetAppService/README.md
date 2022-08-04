# Tweet Service

Tweet App Service is Rest Api developed with Springboot. Consist of multiple Api. Will be implemented for UI with Angular.

- Springboot
- Junit
- Mockito
- LogStach
- Kafka
- SpringValidation

## Features
>- The valid user can log in into the API and post the tweet, reply and like.

## Endpoints 
**1. User Service**
- Get All users
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/users'
```

- Login
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/login?emailId={{emailId}}&password={{[password}}'
```

- Register
```sh
curl --location --request POST 'http://localhost:8080/api/v1.0/tweets/register' \
--data-raw '{
    "userId": 5,
    "firstName": "{{firstName}}",
    "lastName": "{{lastName}}",
    "gender": "{{gender}}",
    "dob": "{{DOB}}",
    "emailId": "{{emailId}}",
    "password": "{{password}}"
}'
```

- Forgot Password
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/forgot?userName={{emailId}}&newPassword={{password}}'
```

- searchByUserName
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/users/search?userName={{emailId}}'
```
**2. Tweet Service**
- allTweets
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/all'
```
- addTweet
```sh
curl --location --request POST 'http://localhost:8080/api/v1.0/tweets/add/{{emailId}}' \
--data-raw '{
    "userName": "{{emailId}}",
    "tweetId": 3,
    "tweet": "hi"
}'
```
- deleteTweet
```sh
curl --location --request DELETE 'http://localhost:8080/api/v1.0/tweets/{{emailId}}/delete/{{tweetId}}'
```
- replyTweet
```sh
curl --location --request POST 'http://localhost:8080/api/v1.0/tweets/{{emailId}}/reply/{{tweetId}}/hello'
```
- likeTweet
```sh
curl --location --request PUT 'http://localhost:8080/api/v1.0/tweets/{{emailId}}/like/{{tweetId}'
```
- updateTweet
```sh
curl --location --request PUT 'http://localhost:8080/api/v1.0/tweets/{{emailId}}/update/{{tweetId}}' \
--data-raw '{
    "tweet": "Hello",
    "tweetId": 1,
    "userName": "{{emailId}}"
}'
```
- allUserTweet
```sh
curl --location --request GET 'http://localhost:8080/api/v1.0/tweets/{{emailId}}'
```
**3. SwaggerAPI Docs**
- Swagger Api Docs
```sh
curl --location --request GET 'http://localhost:8080/v2/api-docs'
```
# TweetApp
