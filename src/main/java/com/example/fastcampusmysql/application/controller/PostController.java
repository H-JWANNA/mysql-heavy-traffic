package com.example.fastcampusmysql.application.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fastcampusmysql.application.usecase.CreatePostUseCase;
import com.example.fastcampusmysql.application.usecase.GetTimelinePostsUseCase;
import com.example.fastcampusmysql.domain.common.util.CursorRequest;
import com.example.fastcampusmysql.domain.common.util.PageCursor;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCount;
import com.example.fastcampusmysql.domain.post.dto.DailyPostCountRequest;
import com.example.fastcampusmysql.domain.post.dto.PostCommand;
import com.example.fastcampusmysql.domain.post.dto.PostDto;
import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.service.PostReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
	private final PostReadService postReadService;
	private final GetTimelinePostsUseCase getTimelinePostsUseCase;
	private final CreatePostUseCase createPostUseCase;

	@PostMapping
	public PostDto create(PostCommand command) {
		// TODO: 회원 유효성 검증
		// TODO: 이미지 업로드, 비디오 업로드, 게시물 수정, 댓글 등 부가 기능 추후에 추가
		return createPostUseCase.execute(command);
	}

	@GetMapping("/daily-post-counts")
	public List<DailyPostCount> getDailyPostCounts(DailyPostCountRequest request) {

		return postReadService.getDailyPostCount(request);
	}

	@GetMapping("/members/{memberId}")
	public Page<Post> getPosts(
		@PathVariable Long memberId,
		Pageable pageable
	) {
		return postReadService.getPosts(memberId, pageable);
	}

	@GetMapping("/members/{memberId}/by-cursor")
	public PageCursor<Post> getPostsByCursor(
		@PathVariable Long memberId,
		CursorRequest cursorRequest
	) {
		return postReadService.getPosts(memberId, cursorRequest);
	}

	@GetMapping("/members/{memberId}/timeline")
	public PageCursor<Post> getTimeline(
		@PathVariable Long memberId,
		CursorRequest cursorRequest
	) {
		return getTimelinePostsUseCase.executeByTimeline(memberId, cursorRequest);
	}
}
