package com.example.fastcampusmysql.domain.post.repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
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
		.likeCount(resultSet.getLong("likeCount"))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public Post save(Post post) {
		if (post.getId() == null) {
			return insert(post);
		}

		return update(post);
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

	private Post update(Post post) {
		String sql = String.format("""
			UPDATE %s SET
			memberId = :memberId,
			contents = :contents,
			createdDate = :createdDate,
			likeCount = :likeCount,
			createdAt = :createdAt
			WHERE id = :id
			""", TABLE);
		SqlParameterSource params = new BeanPropertySqlParameterSource(post);
		namedParameterJdbcTemplate.update(sql, params);

		return post;
	}

	public Optional<Post> findById(Long postId, Boolean requiredLock) {
		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE id = :id
			""", TABLE);

		if (requiredLock) {
			sql += " FOR UPDATE";
		}

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("id", postId);

		try {
			Post post = namedParameterJdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
			return Optional.ofNullable(post);
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}

	}

	public List<Post> findAllByInId(List<Long> ids) {
		if (ids.isEmpty()) {
			return List.of();
		}

		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE id IN (:ids)
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("ids", ids);

		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
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

	public List<Post> findAllByInMemberIdsAndOrderByIdDesc(List<Long> memberIds, int size) {
		if (memberIds.isEmpty()) {
			return List.of();
		}

		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE memberId in (:memberIds)
			ORDER BY id desc
			LIMIT :size
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberIds", memberIds)
			.addValue("size", size);

		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
	}

	public List<Post> findAllByLessThanIdAndInMemberIdsAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {
		if (memberIds.isEmpty()) {
			return List.of();
		}

		String sql = String.format("""
			SELECT *
			FROM %s
			WHERE memberId in (:memberIds) AND id < :id
			ORDER BY id desc
			LIMIT :size
			""", TABLE);

		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("memberIds", memberIds)
			.addValue("id", id)
			.addValue("size", size);

		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
	}
}
