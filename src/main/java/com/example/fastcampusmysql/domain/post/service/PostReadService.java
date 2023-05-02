package com.example.fastcampusmysql.domain.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.fastcampusmysql.domain.common.util.CursorRequest;
import com.example.fastcampusmysql.domain.common.util.PageCursor;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostReadService {
	private final PostRepository postRepository;

	public List<DailyPostCount> getDailyPostCount(DailyPostCountRequest request) {

		return postRepository.groupByCreatedDate(request);
	}

	public Page<Post> getPosts(Long memberId, Pageable pageable) {
		return postRepository.findAllByMemberId(memberId, pageable);
	}

	public PageCursor<Post> getPosts(Long memberId, CursorRequest cursorRequest) {
		var posts = findAllBy(memberId, cursorRequest);

		// 마지막 Key 인지는 알 필요가 없지만, 마지막 데이터인지는 알아야한다.
		Long nextKey = posts.stream()
			.mapToLong(Post::getId)
			.min()
			.orElse(CursorRequest.NONE_KEY);

		return new PageCursor<>(cursorRequest.next(nextKey), posts);
	}

	private List<Post> findAllBy(Long memberId, CursorRequest cursorRequest) {
		// 클라이언트가 처음 데이터를 요청할 때는 Key 가 없으므로 Default Key 설정
		if (cursorRequest.hasKey()) {
			return postRepository.findAllByLessThanIdAndMemberIdAndOrderByIdDesc(
				cursorRequest.key(), memberId, cursorRequest.size());
		}

		return postRepository.findAllByMemberIdAndOrderByIdDesc(memberId, cursorRequest.size());
	}
}
