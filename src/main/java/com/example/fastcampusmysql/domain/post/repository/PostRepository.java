package com.example.fastcampusmysql.domain.post.repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;

import com.example.fastcampusmysql.domain.common.util.PageHelper;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PostRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	final static String TABLE = "Post";

	private final static RowMapper<DailyPostCount> DAILY_POST_COUNT_MAPPER = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
		resultSet.getLong("memberId"),
		resultSet.getObject("createdDate", LocalDate.class),
		resultSet.getLong("count")
	);

	private final static RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
		.id(resultSet.getLong("id"))
		.memberId(resultSet.getLong("memberId"))
		.contents(resultSet.getString("contents"))
		.createdDate(resultSet.getObject("createdDate", LocalDate.class))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public Post save(Post post) {
		if (post.getId() == null) {
			return insert(post);
		}

		// 현재는 수정 기능 X
		throw new UnsupportedOperationException("Post는 갱신을 지원하지 않습니다.");
	}

	private Post insert(Post post) {
		SimpleJdbcInsertOperations simpleJdbcInsert =
			new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
				.withCatalogName("fast_sns")
				.withTableName(TABLE)                          // INSERT INTO 'TABLE'
				.usingGeneratedKeyColumns("id");    // AUTO_INCREMENT
		SqlParameterSource params = new BeanPropertySqlParameterSource(post);

		long id = simpleJdbcInsert
			.executeAndReturnKey(params)
			.longValue();

		return Post
			.builder()
			.id(id)
			.memberId(post.getMemberId())
			.contents(post.getContents())
			.createdDate(post.getCreatedDate())
			.createdAt(post.getCreatedAt())
			.build();
	}

	public void bulkInsert(List<Post> posts) {
		String sql = String.format("""
			INSERT INTO %s (memberId, contents, createdDate, createdAt)
			VALUES (:memberId, :contents, :createdDate, :createdAt)
			""", TABLE);

		SqlParameterSource[] params = posts
			.stream()
			.map(BeanPropertySqlParameterSource::new)
			.toArray(SqlParameterSource[]::new);

		namedParameterJdbcTemplate.batchUpdate(sql, params);
	}

	public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
		String sql = String.format("""
				SELECT createdDate, memberId, count(id) as count
				FROM %s
				WHERE memberId = :memberId and createdDate BETWEEN :firstDate and :lastDate
				GROUP BY memberId, createdDate
			""", TABLE);

		SqlParameterSource params = new BeanPropertySqlParameterSource(request);

		return namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_MAPPER);
	}

	public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {
		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE memberId = :memberId
			ORDER BY %s
			LIMIT :size
			OFFSET :offset
			""", TABLE, PageHelper.orderBy(pageable.getSort()));

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberId", memberId)
			.addValue("size", pageable.getPageSize())
			.addValue("offset", pageable.getOffset());

		List<Post> posts = namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);

		return new PageImpl<>(posts, pageable, getCount(memberId));
	}

	private Long getCount(Long memberId) {
		String sql = String.format("""
			SELECT COUNT(id)
			FROM %s
			WHERE memberId = :memberId
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberId", memberId);

		return namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
	}

	public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
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

	public List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
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
