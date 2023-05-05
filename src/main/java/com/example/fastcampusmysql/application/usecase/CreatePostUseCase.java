package com.example.fastcampusmysql.application.usecase;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.post.dto.PostDto;
import com.example.fastcampusmysql.domain.post.service.PostWriteService;
import com.example.fastcampusmysql.domain.post.service.TimelineWriteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CreatePostUseCase {
	private final FollowReadService followReadService;

	private final PostWriteService postWriteService;
	private final TimelineWriteService timelineWriteService;

	public PostDto execute(PostCommand command) {
		var post = postWriteService.create(command);

		var followerMemberIds = followReadService
			.getFollowers(post.memberId())
			.stream()
			.map(FollowDto::fromMemberId)
			.toList();

		timelineWriteService.deliveryToTimeline(post.id(), followerMemberIds);

		return post;
	}
}
