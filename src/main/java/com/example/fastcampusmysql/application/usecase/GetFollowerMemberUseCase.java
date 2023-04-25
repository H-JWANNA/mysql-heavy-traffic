package com.example.fastcampusmysql.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.service.FollowReadService;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.service.MemberReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GetFollowerMemberUseCase {
	private final MemberReadService memberReadService;
	private final FollowReadService followReadService;

	public List<MemberDto> execute(Long memberId) {
		List<FollowDto> followers = followReadService.getFollowers(memberId);
		List<Long> followerMemberIds = followers.stream()
			.map(FollowDto::fromMemberId)
			.collect(Collectors.toList());

		return memberReadService.getMembers(followerMemberIds);
	}
}
