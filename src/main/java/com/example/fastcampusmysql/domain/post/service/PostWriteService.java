package com.example.fastcampusmysql.domain.post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fastcampusmysql.domain.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.post.dto.PostDto;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.mapper.PostMapper;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostWriteService {
	private final PostMapper mapper;
	private final PostRepository postRepository;

	public PostDto create(PostCommand command) {
		Post post = Post.builder()
			.memberId(command.memberId())
			.contents(command.contents())
			.build();

		Post savedPost = postRepository.save(post);

		return mapper.toDto(savedPost);
	}

	@Transactional
	public void likePost(Long postId) {
		var post = postRepository.findById(postId).orElseThrow();
		post.incrementLikeCount();
		postRepository.save(post);
	}
}
