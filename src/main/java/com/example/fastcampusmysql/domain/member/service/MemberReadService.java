package com.example.fastcampusmysql.domain.member.service;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberReadService {
	private final MemberRepository memberRepository;

	public Member getMember(long id) {

		return memberRepository.findById(id).orElseThrow();
	}
}
