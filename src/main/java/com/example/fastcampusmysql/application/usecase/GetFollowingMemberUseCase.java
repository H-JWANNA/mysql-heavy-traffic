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
public class GetFollowingMemberUseCase {
	private final MemberReadService memberReadService;
	private final FollowReadService followReadService;

	public List<MemberDto> execute(Long memberId) {
		List<FollowDto> followings = followReadService.getFollowings(memberId);
		List<Long> followingMemberIds = followings.stream()
			.map(FollowDto::toMemberId)
			.collect(Collectors.toList());

		return memberReadService.getMembers(followingMemberIds);
	}
}
