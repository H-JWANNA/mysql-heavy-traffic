package com.example.fastcampusmysql.domain.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.fastcampusmysql.domain.member.entity.Member;
import com.example.fastcampusmysql.domain.util.MemberFixtureFactory;

public class MemberTest {

	@DisplayName("회원은 닉네임을 변경할 수 있다")
	@Test
	public void testChangeName() {
		Member member = MemberFixtureFactory.create();
		String expected = "JWANNA";

		member.changeNickname(expected);

		Assertions.assertEquals(expected, member.getNickname());
	}

	@DisplayName("회원의 닉네임은 10자를 초과할 수 없다")
	@Test
	public void testNicknameMaxLength() {
		Member member = MemberFixtureFactory.create();
		String overMaxLengthNickname = "MYNICKNAMEISJWANNA";

		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> member.changeNickname(overMaxLengthNickname)
		);
	}
}
