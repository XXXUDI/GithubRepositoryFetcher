package com.socompany.githubrepofetcher.mapper;

import com.socompany.githubrepofetcher.model.GithubRepository;
import com.socompany.githubrepofetcher.model.dto.GithubRepositoryDto;
import org.springframework.stereotype.Component;

@Component
public class GithubRepositoryDtoMapper implements Mapper<GithubRepository, GithubRepositoryDto> {

    @Override
    public GithubRepositoryDto map(GithubRepository from) {
        return new GithubRepositoryDto(from.getName(), from.getOwner().getLogin(), null);
    }
}
