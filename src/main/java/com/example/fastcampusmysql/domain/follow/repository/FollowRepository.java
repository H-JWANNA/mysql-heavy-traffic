package com.example.fastcampusmysql.domain.follow.repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.example.fastcampusmysql.domain.follow.entity.Follow;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class FollowRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	final static String TABLE = "Follow";
	final static RowMapper<Follow> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Follow
		.builder()
		.id(resultSet.getLong("id"))
		.fromMemberId(resultSet.getLong("fromMemberId"))
		.toMemberId(resultSet.getLong("toMemberId"))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public Follow save(Follow follow) {
		if (follow.getId() == null) {
			return insert(follow);
		}

		throw new UnsupportedOperationException("Follow는 갱신을 지원하지 않습니다.");
	}

	public Follow insert(Follow follow) {
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
			.withCatalogName("fast_sns")
			.withTableName(TABLE)
			.usingGeneratedKeyColumns("id");

		SqlParameterSource params = new BeanPropertySqlParameterSource(follow);
		Long id = jdbcInsert.executeAndReturnKey(params).longValue();

		return Follow.builder()
			.id(id)
			.fromMemberId(follow.getFromMemberId())
			.toMemberId(follow.getToMemberId())
			.createdAt(follow.getCreatedAt())
			.build();
	}
}
