package com.example.community.dto;

import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;


}
