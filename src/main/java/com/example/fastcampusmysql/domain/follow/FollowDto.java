package com.example.fastcampusmysql.domain.follow;

import java.time.LocalDateTime;

public record FollowDto(
	Long id,
	Long fromMemberId,
	Long toMemberId,
	LocalDateTime createdAt
) {
}
