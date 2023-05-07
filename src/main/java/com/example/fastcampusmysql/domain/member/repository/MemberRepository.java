package com.example.fastcampusmysql.domain.member.repository;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;

import com.example.fastcampusmysql.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class MemberRepository {
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	final static String TABLE = "Member";
	final static RowMapper<Member> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Member
		.builder()
		.id(resultSet.getLong("id"))
		.email(resultSet.getString("email"))
		.nickname(resultSet.getString("nickname"))
		.birthDay(resultSet.getObject("birthDay", LocalDate.class))
		.createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
		.build();

	public Member save(Member member) {
		/*
			member id를 보고 갱신 또는 삽입을 결정
			반환값은 id를 담아서 반환한다.
		 */

		if (member.getId() == null) {
			return insert(member);
		}

		return update(member);
	}

	private Member insert(Member member) {
		SimpleJdbcInsertOperations simpleJdbcInsert =
			new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
				.withCatalogName("fast_sns")
				.withTableName(TABLE)                          // INSERT INTO 'TABLE'
				.usingGeneratedKeyColumns("id");    // AUTO_INCREMENT
		SqlParameterSource params = new BeanPropertySqlParameterSource(member);

		long id = simpleJdbcInsert
			.executeAndReturnKey(params)
			.longValue();

		return Member
			.builder()
			.id(id)
			.email(member.getEmail())
			.nickname(member.getNickname())
			.birthDay(member.getBirthDay())
			.createdAt(member.getCreatedAt())
			.build();
	}

	private Member update(Member member) {
		String sql = String.format("""
			UPDATE %s SET
			email = :email,
			nickname = :nickname,
			birthDay = :birthDay
			WHERE id = :id
			""", TABLE);
		SqlParameterSource params = new BeanPropertySqlParameterSource(member);
		namedParameterJdbcTemplate.update(sql, params);

		return member;
	}

	public Optional<Member> findById(Long id) {
		String sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
		SqlParameterSource params = new MapSqlParameterSource()
			.addValue("id", id);

		try {
			Member member = namedParameterJdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
			return Optional.ofNullable(member);
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	public List<Member> findAllByIdIn(List<Long> ids) {
		if (ids.isEmpty()) {
			return List.of();
		}

		String sql = String.format("SELECT * FROM %s WHERE id in (:ids)", TABLE);
		SqlParameterSource params = new MapSqlParameterSource().addValue("ids", ids);
		return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
	}
}
