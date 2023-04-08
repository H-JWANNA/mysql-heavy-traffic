package com.example.fastcampusmysql.global.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

// @Configuration
public class AppContextConfig {
	// @Bean
	public DataSource dataSource() {
		DataSource dataSource = new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.setScriptEncoding("UTF-8")
			.addScript("classpath:schema.ddl.sql")
			.build();

		return dataSource;
	}
}
