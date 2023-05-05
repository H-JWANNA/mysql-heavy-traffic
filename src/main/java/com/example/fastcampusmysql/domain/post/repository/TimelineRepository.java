package com.example.fastcampusmysql.domain.post.repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;

import com.example.fastcampusmysql.domain.post.entity.Timeline;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class TimelineRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	final static String TABLE = "Timeline";

	private final static RowMapper<Timeline> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Timeline.builder()
		.id(resultSet.getLong("id"))
		.memberId(resultSet.getLong("memberId"))
		.postId(resultSet.getLong("postId"))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public Timeline save(Timeline timeline) {
		if (timeline.getId() == null) {
			return insert(timeline);
		}

		throw new UnsupportedOperationException("Timeline은 갱신을 지원하지 않습니다.");
	}

	public Timeline insert(Timeline timeline) {
		SimpleJdbcInsertOperations simpleJdbcInsert =
			new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
				.withCatalogName("fast_sns")
				.withTableName(TABLE)
				.usingGeneratedKeyColumns("id");
		SqlParameterSource params = new BeanPropertySqlParameterSource(timeline);

		long id = simpleJdbcInsert
			.executeAndReturnKey(params)
			.longValue();

		return Timeline.builder()
			.id(id)
			.memberId(timeline.getMemberId())
			.postId(timeline.getPostId())
			.createdAt(timeline.getCreatedAt())
			.build();
	}

	public void bulkInsert(List<Timeline> timelines) {
		String sql = String.format("""
			INSERT INTO %s (memberId, postId, createdAt)
			VALUES (:memberId, :postId, :createdAt)
			""", TABLE);

		SqlParameterSource[] params = timelines
			.stream()
			.map(BeanPropertySqlParameterSource::new)
			.toArray(SqlParameterSource[]::new);

		namedParameterJdbcTemplate.batchUpdate(sql, params);
	}

	public List<Timeline> findAllByMemberIdOrderByIdDesc(Long memberId, int size) {
		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE memberId = :memberId
			ORDER BY id desc
			LIMIT :size
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberId", memberId)
			.addValue("size", size);

		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
	}

	public List<Timeline> findAllByLessThanIdAndMemberIdOrderByIdDesc(Long id, Long memberId, int size) {
		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE memberId = :memberId AND id < :id
			ORDER BY id desc
			LIMIT :size
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberId", memberId)
			.addValue("id", id)
			.addValue("size", size);

		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
	}
}
