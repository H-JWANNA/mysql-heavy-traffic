package com.example.fastcampusmysql.domain.member.service;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.member.dto.RegisterMemberCommand;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberWriteService {
	private final MemberRepository memberRepository;

	public Member create(RegisterMemberCommand command) {
		/*
			회원 정보 등록 (이메일, 닉네임, 생년월일)
				- 닉네임은 10자를 넘길 수 없다.
			파라미터 - memberRegisterCommand

			val member = Member.of(memberRegisterCommand)
			memberRepository.save()
		 */

		var member = Member.builder()
			.email(command.email())
			.nickname(command.nickname())
			.birthDay(command.birthDay())
			.build();

		return memberRepository.save(member);
	}
}
