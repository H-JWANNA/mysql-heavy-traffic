package com.example.fastcampusmysql.domain.post;

import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import com.example.fastcampusmysql.domain.post.entity.Post;
import com.example.fastcampusmysql.domain.post.repository.PostRepository;
import com.example.fastcampusmysql.domain.util.PostFixtureFactory;

@SpringBootTest
public class PostBulkInsertTest {
	@Autowired
	private PostRepository postRepository;

	@Test
	public void bulkInsert() {
		var easyRandom = PostFixtureFactory.get(
			2L,
			LocalDate.of(2023, 4, 1),
			LocalDate.of(2023, 5, 1));

		var stopWatch = new StopWatch();
		stopWatch.start();

		int _1만 = 10000;
		var posts = IntStream.range(0, _1만 * 100)
			.parallel()
			.mapToObj(i -> easyRandom.nextObject(Post.class))
			.toList();

		stopWatch.stop();
		System.out.println("객체 생성 시간 : " + stopWatch.getTotalTimeSeconds());

		var queryStopWatch = new StopWatch();
		queryStopWatch.start();

		postRepository.bulkInsert(posts);

		queryStopWatch.stop();
		System.out.println("쿼리 실행 시간 : " + queryStopWatch.getTotalTimeSeconds());
	}
}
