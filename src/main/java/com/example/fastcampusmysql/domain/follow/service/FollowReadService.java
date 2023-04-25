package com.example.fastcampusmysql.domain.follow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.mapper.FollowMapper;
import com.example.fastcampusmysql.domain.follow.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FollowReadService {
	private final FollowMapper mapper;
	private final FollowRepository followRepository;

	// 내가 Follow 하는 사람 (From me To others)
	public List<FollowDto> getFollowings(Long memberId) {
		return followRepository.findFollowingByMemberId(memberId)
			.stream()
			.map(mapper::toDto)
			.collect(Collectors.toList());
	}

	// 나를 Follow 하는 사람 (From others To me)
	public List<FollowDto> getFollowers(Long memberId) {
		return followRepository.findFollowerByMemberId(memberId)
			.stream()
			.map(mapper::toDto)
			.collect(Collectors.toList());
	}
}
