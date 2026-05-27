# GitHub Repo Fetcher

## Purpose
This project provides a service to fetch a user's GitHub repositories, excluding any repositories that are forked. It also retrieves the corresponding branches and their latest commit hashes for each repository.

## API Endpoint
`GET /api/v1/repositories/{username}`

Returns a list of repositories, their owner login, branch names, and their last commit SHAs.
In case the user does not exist, it returns a 404 error response.

### Example Output
```json
[
  {
    "repositoryName": "GithubRepositoryFetcher",
    "ownerLogin": "XXXUDI",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "0f5f1fcc2d0455e6de4bf00ccfe66b7f64fa94b4"
      }
    ]
  }
]
```

## How to Run Locally

### Clone the Repository
```bash
git clone https://github.com/XXXUDI/GithubRepositoryFetcher.git
cd GithubRepositoryFetcher
```

### Run the Application
This is a Spring Boot application built with Gradle. You can run it from the root directory using:

```bash
./gradlew bootRun
```

(Requires JDK 25 and Gradle)

The application will start on `http://localhost:8080`

### Test the API
Once the application is running, you can test it using curl:

```bash
curl http://localhost:8080/api/v1/repositories/XXXUDI
```

Or open in your browser:
```
http://localhost:8080/api/v1/repositories/XXXUDI
```

### Run Tests
To run the test suite:

```bash
./gradlew test
```
