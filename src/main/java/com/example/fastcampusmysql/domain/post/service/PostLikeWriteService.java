package com.example.fastcampusmysql.domain.post.service;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.entity.PostLike;
import com.example.fastcampusmysql.domain.post.repository.PostLikeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostLikeWriteService {
	private final PostLikeRepository postLikeRepository;

	public Long create(Post post, MemberDto member) {
		PostLike postLike = PostLike.builder()
			.memberId(member.id())
			.postId(post.getId())
			.build();

		return postLikeRepository.save(postLike).getPostId();
	}
}
