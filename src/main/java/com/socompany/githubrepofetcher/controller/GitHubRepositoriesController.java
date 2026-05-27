package com.socompany.githubrepofetcher.controller;

import com.socompany.githubrepofetcher.model.dto.GithubRepositoryDto;
import com.socompany.githubrepofetcher.service.GitHubRepositoryFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor // Lombok constructor
@RequestMapping("/api/v1/repositories") // Basic endpoint path
@RestController
@Slf4j // Logger
public class GitHubRepositoriesController {

    private final GitHubRepositoryFetcherService service;

    // GET /api/v1/repositories/{user}
    @GetMapping("/{user}")
    public ResponseEntity<List<GithubRepositoryDto>> getRepositories(@PathVariable String user) throws IOException, InterruptedException {
        log.info("Received a request for user: {}", user);
        var repositories = service.getRepositories(user);
        return ResponseEntity.ok(repositories);
    }
}
