package com.example.fastcampusmysql.domain.member.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.MemberNicknameHistoryDto;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.mapper.MemberMapper;
import com.example.fastcampusmysql.domain.member.repository.MemberNIcknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberReadService {
	private final MemberMapper mapper;
	private final MemberRepository memberRepository;
	private final MemberNIcknameHistoryRepository historyRepository;

	public MemberDto getMember(long id) {
		Member member = memberRepository.findById(id).orElseThrow();

		return mapper.toDto(member);
	}

	public List<MemberNicknameHistoryDto> getNicknameHistories(Long memberId) {
		return historyRepository.findALlByMemberId(memberId)
			.stream()
			.map(mapper::toDto)
			.collect(Collectors.toList());
	}

	public List<MemberDto> getMembers(List<Long> ids) {
		return memberRepository.findAllByIdIn(ids)
			.stream()
			.map(mapper::toDto)
			.collect(Collectors.toList());
	}
}
