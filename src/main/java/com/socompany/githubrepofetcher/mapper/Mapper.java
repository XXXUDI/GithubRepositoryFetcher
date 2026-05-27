package com.socompany.githubrepofetcher.mapper;
// Mapper | F - From object ; T - To object
public interface Mapper<F, T> {

    T map(F from);
}
