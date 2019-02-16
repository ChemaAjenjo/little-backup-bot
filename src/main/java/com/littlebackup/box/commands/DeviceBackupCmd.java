package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.MOUNT_CMD;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.littlebackup.box.config.FolderConfig;

/** Backup from local storage to storage device */

public class DeviceBackupCmd implements Command {

	public static final String TAG = DeviceBackupCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {

		try {
			Path storagePath = Paths.get(FolderConfig.DEV_SDA1);

			// Wait for a USB storage device (e.g., a USB flash drive)
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

			Files.createDirectories(Paths.get(FolderConfig.MOUNT_POINT_STORAGE + File.separator + "BACKUP"));

			// Perform backup using rsync
			bot.execute(new SendMessage().setParseMode(ParseMode.MARKDOWN).setChatId(chatId)
					.setText("Making backup... Please wait."));
			RSync rsync = new RSync().source(FolderConfig.HOME_DIR + File.separator)
					.destination(FolderConfig.MOUNT_POINT_STORAGE + File.separator + "BACKUP").archive(true)
					.verbose(true).exclude(new String[] { "*.id", "*.dat", "IndexerVolumeGuid" });
			CollectingProcessOutput output = rsync.execute();

			BotLogger.info(TAG, "Backup from " + FolderConfig.MOUNT_POINT_CARD + " finished with exit code = "
					+ String.valueOf(output.getExitCode()));

			if (output.getExitCode() > 0) {
				BotLogger.error(TAG, output.getStdErr());
				return output.getStdErr();
			} else {
				// Remove local backup folder
				FileUtils.deleteDirectory(new File(FolderConfig.HOME_DIR));
			}

			// Runtime.getRuntime().exec(new String[] { "chmod", "-R", "a=rwx", backupPath
			// }).waitFor();

			BotLogger.info(TAG, output.getStdOut());
			return "BACKUP TO DEVICE: " + System.lineSeparator() + output.getStdOut();

		} catch (Exception e) {
			BotLogger.error(TAG, e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
	}

}