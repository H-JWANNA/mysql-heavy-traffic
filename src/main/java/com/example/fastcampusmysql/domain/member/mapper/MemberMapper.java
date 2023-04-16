package com.example.fastcampusmysql.domain.member.mapper;

import org.springframework.stereotype.Component;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.entity.Member;

@Component
public class MemberMapper {
	public MemberDto toDto(Member member) {
		return new MemberDto(member.getId(), member.getEmail(), member.getNickname(), member.getBirthDay());
	}
}
