package com.example.fastcampusmysql.domain.post.repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;

import com.example.fastcampusmysql.domain.post.entity.PostLike;
import com.example.fastcampusmysql.domain.post.entity.Timeline;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PostLikeRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	final static String TABLE = "PostLike";

	private final static RowMapper<Timeline> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Timeline.builder()
		.id(resultSet.getLong("id"))
		.memberId(resultSet.getLong("memberId"))
		.postId(resultSet.getLong("postId"))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public PostLike save(PostLike postLike) {
		if (postLike.getId() == null) {
			return insert(postLike);
		}

		throw new UnsupportedOperationException("PostLike는 갱신을 지원하지 않습니다.");
	}

	public PostLike insert(PostLike postLike) {
		SimpleJdbcInsertOperations simpleJdbcInsert =
			new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
				.withCatalogName("fast_sns")
				.withTableName(TABLE)
				.usingGeneratedKeyColumns("id");

		SqlParameterSource params = new BeanPropertySqlParameterSource(postLike);

		long id = simpleJdbcInsert
			.executeAndReturnKey(params)
			.longValue();

		return PostLike.builder()
			.id(id)
			.memberId(postLike.getMemberId())
			.postId(postLike.getPostId())
			.createdAt(postLike.getCreatedAt())
			.build();
	}

	public Long count(Long postId) {
		String sql = String.format("""
			SELECT COUNT(id)
			FROM %s
			WHERE postId = :postId
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("postId", postId);

		return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
	}
}
