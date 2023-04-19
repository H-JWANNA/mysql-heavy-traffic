package com.example.fastcampusmysql.domain.member.mapper;

import org.springframework.stereotype.Component;

import com.example.fastcampusmysql.domain.member.dto.MemberDto;
import com.example.fastcampusmysql.domain.member.dto.MemberNicknameHistoryDto;
import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.member.entity.MemberNicknameHistory;

@Component
public class MemberMapper {
	public MemberDto toDto(Member member) {
		return new MemberDto(
			member.getId(),
			member.getEmail(),
			member.getNickname(),
			member.getBirthDay());
	}

	public MemberNicknameHistoryDto toDto(MemberNicknameHistory history) {
		return new MemberNicknameHistoryDto(
			history.getId(),
			history.getMemberId(),
			history.getNickname(),
			history.getCreatedAt());
	}
}
