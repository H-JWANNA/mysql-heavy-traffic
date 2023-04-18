package com.example.fastcampusmysql.domain.util;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.example.fastcampusmysql.domain.member.entity.Member;

public class MemberFixtureFactory {
	// ObjectMother & EasyRandom 을 사용한 Test Fixture 생성

	public static Member create() {
		EasyRandomParameters param = new EasyRandomParameters();
		return new EasyRandom(param).nextObject(Member.class);
	}

	public static Member create(Long seed) {
		EasyRandomParameters param = new EasyRandomParameters().seed(seed);
		return new EasyRandom(param).nextObject(Member.class);
	}
}
