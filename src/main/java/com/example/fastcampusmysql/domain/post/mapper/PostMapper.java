package com.example.fastcampusmysql.domain.post.mapper;

import org.springframework.stereotype.Component;

import com.example.fastcampusmysql.domain.post.dto.PostDto;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PostMapper {
	private final PostLikeRepository postLikeRepository;

	public PostDto toDto(Post post) {
		return new PostDto(
			post.getId(),
			post.getMemberId(),
			post.getContents(),
			post.getCreatedDate(),
			// post.getLikeCount(),
			postLikeRepository.count(post.getId()),
			post.getCreatedAt());
	}
}
