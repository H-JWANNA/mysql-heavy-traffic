package com.example.fastcampusmysql.domain.follow.mapper;

import org.springframework.stereotype.Component;

import com.example.fastcampusmysql.domain.follow.FollowDto;
import com.example.fastcampusmysql.domain.follow.entity.Follow;

@Component
public class FollowMapper {
	public FollowDto toDto(Follow follow) {
		return new FollowDto(
			follow.getId(),
			follow.getFromMemberId(),
			follow.getToMemberId(),
			follow.getCreatedAt()
		);
	}
}
