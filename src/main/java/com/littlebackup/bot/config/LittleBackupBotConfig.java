package com.littlebackup.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({ "classpath:little-backup-bot.properties" })
public class LittleBackupBotConfig {

	@Value("${bot.token}")
	private String botToken;
	@Value("${bot.username}")
	private String botUserName;
	@Value("${bot.creatorid}")
	private Long creatorId;

	@Bean
	public String getBotToken() {
		return this.botToken;
	}

	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	@Bean
	public String getBotUserName() {
		return this.botUserName;
	}

	public void setBotUserName(String botUserName) {
		this.botUserName = botUserName;
	}

	@Bean
	public Long getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public String toString() {
		return "LittleBackupBotConfig: botToken: \"" + this.botToken + "\", botUserName: \"" + this.botUserName
				+ "\"; creatorId: \"" + this.creatorId + "\"; ";
	}
}