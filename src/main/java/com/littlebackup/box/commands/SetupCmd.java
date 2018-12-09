package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.CARD_MOUNT_POINT;
import static com.littlebackup.utils.Constants.HOME_DIR;
import static com.littlebackup.utils.Constants.MICROSD_MOUNT_POINT;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.telegram.telegrambots.meta.logging.BotLogger;

public class SetupCmd implements Command {

	public static final String TAG = SetupCmd.class.getSimpleName();

	public String executeCommand() {

		try {
			String output = "Created folders: " + System.lineSeparator();

			if (!Files.exists(Paths.get(CARD_MOUNT_POINT))) {

				output = output + Files.createDirectories(Paths.get(CARD_MOUNT_POINT)).toString()
						+ System.lineSeparator();
				BotLogger.info(TAG, "Created " + CARD_MOUNT_POINT);
			}
			if (!Files.exists(Paths.get(MICROSD_MOUNT_POINT))) {
				output = output + Files.createDirectories(Paths.get(MICROSD_MOUNT_POINT)).toString()
						+ System.lineSeparator();
				BotLogger.info(TAG, "Created " + MICROSD_MOUNT_POINT);
			}
			if (!Files.exists(Paths.get(HOME_DIR))) {
				output = output + Files.createDirectories(Paths.get(HOME_DIR)).toString() + System.lineSeparator();
				BotLogger.info(TAG, "Created " + HOME_DIR);
			}

			return output;

		} catch (IOException e) {
			e.printStackTrace();
			BotLogger.error(TAG, e.getMessage());
			return e.getMessage();
		}
	}

}