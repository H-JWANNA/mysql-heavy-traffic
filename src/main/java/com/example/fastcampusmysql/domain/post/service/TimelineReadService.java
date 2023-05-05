package com.example.fastcampusmysql.domain.post.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.common.util.CursorRequest;
import com.example.fastcampusmysql.domain.common.util.PageCursor;
import com.example.fastcampusmysql.domain.post.entity.Timeline;
import com.example.fastcampusmysql.domain.post.repository.TimelineRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TimelineReadService {
	private final TimelineRepository timelineRepository;

	public PageCursor<Timeline> getTimelines(Long memberId, CursorRequest cursorRequest) {
		var timelines = findAllBy(memberId, cursorRequest);
		Long nextKey = getNextKey(timelines);

		return new PageCursor<>(cursorRequest.next(nextKey), timelines);
	}

	private List<Timeline> findAllBy(Long memberId, CursorRequest cursorRequest) {
		if (cursorRequest.hasKey()) {
			return timelineRepository.findAllByLessThanIdAndMemberIdOrderByIdDesc(
				cursorRequest.key(), memberId, cursorRequest.size());
		}

		return timelineRepository.findAllByMemberIdOrderByIdDesc(memberId, cursorRequest.size());
	}

	private static Long getNextKey(List<Timeline> timelines) {
		return timelines.stream()
			.mapToLong(Timeline::getId)
			.min()
			.orElse(CursorRequest.NONE_KEY);
	}
}
