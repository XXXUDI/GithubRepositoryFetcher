# GitHub Repo Fetcher

## Purpose
This project provides a service to fetch a user's GitHub repositories, excluding any repositories that are forked. It also retrieves the corresponding branches and their latest commit hashes for each repository.

## API Endpoint
`GET /api/v1/repositories/{username}`
Returns a list of repositories, their owner login, branch names, and their last commit SHAs.
In case the user does not exist, it returns a 404 error response.

## How to Run
This is a Spring Boot application built with Gradle. You can run it from the root directory using:

```bash
./gradlew bootRun
```
(Requires JDK 25 and Gradle)
