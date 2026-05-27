package com.socompany.githubrepofetcher.service;

import com.socompany.githubrepofetcher.model.dto.GithubRepositoryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.socompany.githubrepofetcher.model.GithubRepository;
import com.socompany.githubrepofetcher.mapper.GithubRepositoryDtoMapper;

@Service
@Slf4j
public class GitHubRepositoryFetcherService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GithubRepositoryDtoMapper mapper;
    private HttpClient httpClient;

    public GitHubRepositoryFetcherService(GithubRepositoryDtoMapper mapper) {
        this.mapper = mapper;
    }

    public List<GithubRepositoryDto> getRepositories(String user) throws IOException, InterruptedException {
        httpClient = HttpClient.newHttpClient();
        var response = httpClient.send(HttpRequest.newBuilder()
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .uri(java.net.URI.create("https://api.github.com/users/" + user + "/repos"))
                .build(), HttpResponse.BodyHandlers.ofString());
        log.info("Response status: {}", response.statusCode());

        if (response.statusCode() == 404) {
            return null;
        }

        List<GithubRepository> repos = objectMapper.readValue(response.body(), new TypeReference<List<GithubRepository>>() {});
        List<GithubRepositoryDto> dtoList = repos.stream()
                .filter(repo -> !repo.isFork())
                .map(mapper::map)
                .collect(Collectors.toList());

        return dtoList;
    }
}
