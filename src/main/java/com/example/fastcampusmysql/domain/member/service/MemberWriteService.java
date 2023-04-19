package com.example.fastcampusmysql.domain.member.service;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.RegisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;
import com.example.fastcampusmysql.domain.member.mapper.MemberMapper;
import com.example.fastcampusmysql.domain.member.repository.MemberNIcknameHistoryRepository;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberWriteService {
	private final MemberMapper mapper;
	private final MemberRepository memberRepository;
	private final MemberNIcknameHistoryRepository historyRepository;

	public MemberDto register(RegisterMemberCommand command) {
		Member member = Member.builder()
			.email(command.email())
			.nickname(command.nickname())
			.birthDay(command.birthDay())
			.build();

		Member savedMember = memberRepository.save(member);
		saveNicknameHistory(savedMember);

		return mapper.toDto(savedMember);
	}

	public void changeNickname(Long id, String nickname) {
		/*
			1. 회원 이름 변경
			2. 변경 내역 저장
		 */
		Member member = memberRepository.findById(id).orElseThrow();
		member.changeNickname(nickname);
		memberRepository.save(member);

		saveNicknameHistory(member);
	}

	private void saveNicknameHistory(Member member) {
		var history = MemberNicknameHistory.builder()
			.memberId(member.getId())
			.nickname(member.getNickname())
			.build();

		historyRepository.save(history);
	}
}
