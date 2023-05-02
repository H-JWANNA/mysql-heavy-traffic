package com.example.fastcampusmysql.domain.common.util;

public record CursorRequest(Long key, Integer size) {
	/*
		Cursor 기반 페이지네이션에서
		1. Key 값은 Unique 보장이 되어야 한다. (중복 X)
		2. 인덱스가 있어야 한다.
		3. 커서 키로 데이터가 정렬이 되어야 한다.
	 */

	// 식별자가 없을 때 사용할 Key
	public final static Long NONE_KEY = -1L;

	public Boolean hasKey() {
		return key != null;
	}

	public CursorRequest next(Long key) {
		return new CursorRequest(key, size);
	}
}
