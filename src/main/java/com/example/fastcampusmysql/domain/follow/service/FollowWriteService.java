package com.example.fastcampusmysql.domain.follow.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.example.fastcampusmysql.domain.follow.entity.Follow;
import com.example.fastcampusmysql.domain.follow.repository.FollowRepository;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FollowWriteService {
	private final FollowRepository followRepository;

	public void create(MemberDto fromMember, MemberDto toMember) {
		/*
			from, to 회원 정보를 받아서 저장
			from <-> to validate
			Long memberId를 받게 되면 여기서 MemberService를 주입 받아서 결합도가 높아진다.
		 */
		Assert.isTrue(!fromMember.id().equals(toMember.id()), "From, To 회원이 동일합니다.");

		Follow follow = Follow.builder()
			.fromMemberId(fromMember.id())
			.toMemberId(toMember.id())
			.build();

		followRepository.save(follow);
	}
}
