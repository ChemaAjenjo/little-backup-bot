package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.SHUTDOWN_CMD;

import java.io.IOException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

public class PowerOffCmd implements Command {

	public static final String TAG = PowerOffCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {
		try {
			bot.execute(new SendMessage().setParseMode(ParseMode.MARKDOWN).setChatId(chatId).setText("Shutdown..."));
			Runtime.getRuntime().exec(new String[] { SHUTDOWN_CMD, "-h", "now" }).waitFor();
		} catch (InterruptedException | IOException | TelegramApiException e) {
			BotLogger.error(TAG, e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}

		return null;
	}

}
