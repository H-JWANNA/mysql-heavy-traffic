package com.example.fastcampusmysql.application.usecase;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.common.util.CursorRequest;
import com.example.fastcampusmysql.domain.common.util.PageCursor;
import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.service.PostReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetTimelinePostsUseCase {
	private final FollowReadService followReadService;
	private final PostReadService postReadService;

	public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
		/*
			1. memberId -> follow 조회
			2. follow memberId로 Post 조회
		 */

		var followings = followReadService.getFollowings(memberId);
		var followingMemberIds = followings.stream().map(FollowDto::toMemberId).toList();

		return postReadService.getPosts(followingMemberIds, cursorRequest);
	}
}
