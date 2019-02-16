package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.MOUNT_CMD;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.littlebackup.config.FolderConfig;
import com.littlebackup.utils.Utils;

/** Backup from sdcard to storage device */

public class CardBackupCmd implements Command {

	public static final String TAG = CardBackupCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {

		try {
			Path storagePath = Paths.get(FolderConfig.DEV_SDA1);
			Path cardPath = Paths.get(FolderConfig.DEV_SDB1);

			bot.execute(new SendMessage().setParseMode(ParseMode.MARKDOWN).setChatId(chatId)
					.setText("Insert *storage device*"));
			while (Files.notExists(storagePath)) {
				storagePath = Paths.get(FolderConfig.DEV_SDA1);
				Thread.sleep(1000L);
			}
			// When the USB storage device is detected, mount it
			Runtime.getRuntime()
					.exec(new String[] { MOUNT_CMD, FolderConfig.DEV_SDA1, FolderConfig.MOUNT_POINT_STORAGE })
					.waitFor();
			BotLogger.info(TAG, "Mounted " + FolderConfig.MOUNT_POINT_STORAGE);

			bot.execute(new SendMessage().setParseMode(ParseMode.MARKDOWN).setChatId(chatId).setText("Insert *card*"));
			while (Files.notExists(cardPath)) {
				cardPath = Paths.get(FolderConfig.DEV_SDB1);
				Thread.sleep(1000L);
			}

			if (Files.exists(cardPath)) {
				// If the card reader is detected, mount it
				BotLogger.debug(TAG, "Mounting " + FolderConfig.DEV_SDB1 + " into " + FolderConfig.MOUNT_POINT_CARD);
				Runtime.getRuntime()
						.exec(new String[] { MOUNT_CMD, FolderConfig.DEV_SDB1, FolderConfig.MOUNT_POINT_CARD })
						.waitFor();

				// Obtain card's UUID
				String label = Utils.getLabelDevice(FolderConfig.DEV_SDB1);

				// Set the backup path
				String backupPath = FolderConfig.MOUNT_POINT_STORAGE + File.separator
						+ Utils.getFileId(FolderConfig.MOUNT_POINT_CARD);

				BotLogger.debug(TAG, "launch rsync command");
				// Perform backup using rsync
				bot.execute(new SendMessage().setParseMode(ParseMode.MARKDOWN).setChatId(chatId)
						.setText("Making backup... Please wait."));
				RSync rsync = new RSync().source(FolderConfig.MOUNT_POINT_CARD + File.separator).destination(backupPath)
						.archive(true).verbose(true).exclude(new String[] { "*.id", "*.dat", "IndexerVolumeGuid" });
				CollectingProcessOutput output = rsync.execute();

				BotLogger.info(TAG, "Backup from " + FolderConfig.MOUNT_POINT_CARD + " finished with exit code = "
						+ String.valueOf(output.getExitCode()));

				if (output.getExitCode() > 0) {
					BotLogger.error(TAG, output.getStdErr());
					return output.getStdErr();
				}

				Runtime.getRuntime().exec(new String[] { "chmod", "-R", "a=rwx", backupPath }).waitFor();

				BotLogger.info(TAG, output.getStdOut());
				return "DEVICE: " + label + System.lineSeparator() + output.getStdOut();
			}

		} catch (Exception e) {
			BotLogger.error(TAG, e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
		return "";
	}

}