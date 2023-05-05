package com.example.fastcampusmysql.application.usecase;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.common.util.CursorRequest;
import com.example.fastcampusmysql.domain.common.util.PageCursor;
import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.entity.Timeline;
import com.example.fastcampusmysql.domain.post.service.PostReadService;
import com.example.fastcampusmysql.domain.post.service.TimelineReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetTimelinePostsUseCase {
	private final FollowReadService followReadService;
	private final PostReadService postReadService;
	private final TimelineReadService timelineReadService;

	public PageCursor<Post> execute(Long memberId, CursorRequest cursorRequest) {
		/*
			Fan Out On Read (Pull Model) : Follow -> Post 순으로 조회하며 읽기 시에 부하
			1. memberId -> follow 조회
			2. follow memberId로 Post 조회
		 */

		var followings = followReadService.getFollowings(memberId);
		var followingMemberIds = followings.stream().map(FollowDto::toMemberId).toList();

		return postReadService.getPosts(followingMemberIds, cursorRequest);
	}

	public PageCursor<Post> executeByTimeline(Long memberId, CursorRequest cursorRequest) {
		/*
			Fan Out On Write (Push Model) : Timeline 테이블을 별도로 만들어 쓰기 시에 부하
			1. Timeline 조회
			2. memberId에 해당하는 Post 조회
		 */
		var pagedTimelines = timelineReadService.getTimelines(memberId, cursorRequest);
		var postIds = pagedTimelines.contents().stream().map(Timeline::getPostId).toList();
		var posts = postReadService.getPostsByIds(postIds);

		return new PageCursor<>(pagedTimelines.nextCursorRequest(), posts);
	}
}
