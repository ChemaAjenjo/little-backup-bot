package com.littlebackup.box.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.littlebackup.box.config.FolderConfig;

/** Setup raspberry pi */

public class SetupCmd implements Command {

	public static final String TAG = SetupCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {

		try {
			String output = "Created folders: " + System.lineSeparator();

			if (!Files.exists(Paths.get(FolderConfig.MOUNT_POINT_CARD))) {

				output = output + Files.createDirectories(Paths.get(FolderConfig.MOUNT_POINT_CARD)).toString()
						+ System.lineSeparator();
				BotLogger.info(TAG, "Created " + FolderConfig.MOUNT_POINT_CARD);
			}
			if (!Files.exists(Paths.get(FolderConfig.MOUNT_POINT_MICROSD))) {
				output = output + Files.createDirectories(Paths.get(FolderConfig.MOUNT_POINT_MICROSD)).toString()
						+ System.lineSeparator();
				BotLogger.info(TAG, "Created " + FolderConfig.MOUNT_POINT_MICROSD);
			}
			if (!Files.exists(Paths.get(FolderConfig.HOME_DIR))) {
				output = output + Files.createDirectories(Paths.get(FolderConfig.HOME_DIR)).toString()
						+ System.lineSeparator();
				BotLogger.info(TAG, "Created " + FolderConfig.HOME_DIR);
			}

			return output;

		} catch (IOException e) {
			BotLogger.error(TAG, e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}

}