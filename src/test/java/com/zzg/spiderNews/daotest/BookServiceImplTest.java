package com.zzg.spiderNews.daotest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zzg.spiderNews.dto.AppointExecution;
import com.zzg.spiderNews.service.BookServiceImpl;

public class BookServiceImplTest extends BaseTest {

	@Autowired
	private BookServiceImpl bookService;

	@Test
	public void testAppoint() throws Exception {
		long bookId = 1001;
		long studentId = 12345678911L;
		AppointExecution execution = bookService.appoint(bookId, studentId);
		System.out.println(execution);
	}
}
