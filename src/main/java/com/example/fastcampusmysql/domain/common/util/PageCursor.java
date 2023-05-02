package com.example.fastcampusmysql.domain.common.util;

import java.util.List;

public record PageCursor<T> (
	CursorRequest nextCursorRequest,
	List<T> contents
) {
}
