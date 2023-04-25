package com.example.fastcampusmysql.application.controller;

import java.util.List;

import javax.validation.constraints.Positive;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fastcampusmysql.application.usecase.CreateFollowMemberUseCase;
import com.example.fastcampusmysql.application.usecase.GetFollowerMemberUseCase;
import com.example.fastcampusmysql.application.usecase.GetFollowingMemberUseCase;
import com.example.fastcampusmysql.domain.member.dto.MemberDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
public class FollowController {
	private final CreateFollowMemberUseCase createFollowMemberUseCase;
	private final GetFollowingMemberUseCase getFollowingMemberUseCase;
	private final GetFollowerMemberUseCase getFollowerMemberUseCase;

	@PostMapping("/{fromId}/{toId}")
	public void create(@PathVariable @Positive Long fromId, @PathVariable @Positive Long toId) {
		createFollowMemberUseCase.execute(fromId, toId);
	}

	@GetMapping("/following/{fromId}")
	public List<MemberDto> getFollowingMembers(@PathVariable @Positive Long fromId) {
		return getFollowingMemberUseCase.execute(fromId);
	}

	@GetMapping("/follower/{toId}")
	public List<MemberDto> getFollowerMembers(@PathVariable @Positive Long toId) {
		return getFollowerMemberUseCase.execute(toId);
	}
}
