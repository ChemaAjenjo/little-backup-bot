package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.CARD_MOUNT_POINT;
import static com.littlebackup.utils.Constants.DEV_SDA1;
import static com.littlebackup.utils.Constants.DEV_SDB1;
import static com.littlebackup.utils.Constants.MOUNT_CMD;
import static com.littlebackup.utils.Constants.STORAGE_MOUNT_POINT;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.littlebackup.utils.Utils;

public class CardBackupCmd implements Command {

	public static final String TAG = CardBackupCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {

		try {
			Path storagePath = Paths.get(DEV_SDA1);
			Path cardPath = Paths.get(DEV_SDB1);

			bot.execute(new SendMessage().setChatId(chatId).setText("Please, insert *storage device*"));
			while (!Files.exists(storagePath)) {
				storagePath = Paths.get(DEV_SDA1);
				Thread.sleep(1000L);
			}
			// When the USB storage device is detected, mount it
			Runtime.getRuntime().exec(new String[] { MOUNT_CMD, DEV_SDA1, STORAGE_MOUNT_POINT }).waitFor();

			bot.execute(new SendMessage().setChatId(chatId).setText("Please, insert *card*"));
			while (!Files.exists(cardPath)) {
				cardPath = Paths.get(DEV_SDB1);
				Thread.sleep(1000L);
			}

			if (Files.exists(cardPath)) {
				// If the card reader is detected, mount it
				BotLogger.debug(TAG, "Mounting " + DEV_SDB1 + " into " + CARD_MOUNT_POINT);
				Runtime.getRuntime().exec(new String[] { MOUNT_CMD, DEV_SDB1, CARD_MOUNT_POINT }).waitFor();

				// Obtain card's UUID
				String label = Utils.getLabelDevice(DEV_SDB1);

				// Set the backup path
				String backupPath = STORAGE_MOUNT_POINT + File.separator + label + File.separator
						+ Utils.getFileId(CARD_MOUNT_POINT);

				BotLogger.debug(TAG, "launch rsync command");
				// Perform backup using rsync
				RSync rsync = new RSync().source(STORAGE_MOUNT_POINT + File.separator).destination(backupPath)
						.archive(true).verbose(true).exclude(new String[] { "*.id", "*.dat", "IndexerVolumeGuid" });
				CollectingProcessOutput output = rsync.execute();

				BotLogger.info(TAG, "Backup from " + CARD_MOUNT_POINT + " finished with exit code = "
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
			e.printStackTrace();
			BotLogger.error(TAG, e.getMessage());
			return e.getMessage();
		}
		return "";
	}

}