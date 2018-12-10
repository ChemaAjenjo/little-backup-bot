package com.littlebackup.box.commands;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public interface Command {

	public String execute(TelegramLongPollingBot bot, Long chatId);

}
