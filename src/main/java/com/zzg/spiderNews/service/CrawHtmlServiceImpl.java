package com.zzg.spiderNews.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zzg.spiderNews.dao.CrawlHtmlDao;
import com.zzg.spiderNews.entity.CrawlHtml;

@Service
public class CrawHtmlServiceImpl {

	@Resource
	CrawlHtmlDao crawlHtmlDao;
	
	public int save(CrawlHtml crawlHtml){
		return crawlHtmlDao.insert(crawlHtml);
	}
	
}