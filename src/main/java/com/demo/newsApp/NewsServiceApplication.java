package com.demo.newsApp;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EntityScan
@Log4j2
@EnableConfigurationProperties
@SpringBootApplication
public class NewsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(NewsServiceApplication.class, args);
	}
}
