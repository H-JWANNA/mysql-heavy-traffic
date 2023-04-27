package com.example.fastcampusmysql.domain.post.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public record DailyPostCountRequest(
	Long memberId,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate firstDate,

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate lastDate
) {
}
