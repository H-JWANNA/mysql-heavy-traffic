package com.example.fastcampusmysql.domain.member.repository;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
				.withTableName("Member")    					  // INSERT INTO 'TABLE'
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

/*
	public Member insertWithBeanPropertySqlParameterSource(Member member) {
		SqlParameterSource params = new BeanPropertySqlParameterSource(member);
		long id = insertMember.executeAndReturnKey(params).longValue();
		return Member
			.builder()
			.id(id)
			.email(member.getEmail())
			.nickname(member.getNickname())
			.birthDay(member.getBirthDay())
			.createdAt(member.getCreatedAt())
			.build();
	}
*/

	private Member update(Member member) {
		// TODO : implemented
		return member;
	}
}
