package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.CARD_MOUNT_POINT;
import static com.littlebackup.utils.Constants.DEV_SDA1;
import static com.littlebackup.utils.Constants.DEV_SDB1;
import static com.littlebackup.utils.Constants.HOME_DIR;
import static com.littlebackup.utils.Constants.MICROSD_MOUNT_POINT;
import static com.littlebackup.utils.Constants.MOUNT_CMD;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.littlebackup.utils.Utils;

/** Backup from multiple reader sdcard to local storage */

public class ReaderBackupCmd implements Command {

	public static final String TAG = ReaderBackupCmd.class.getSimpleName();

	@Override
	public String execute(TelegramLongPollingBot bot, Long chatId) {

		String output = "";

		try {
			Path cardReaderPath = Paths.get(DEV_SDA1);
			Path microSdReaderPath = Paths.get(DEV_SDB1);

			while (Files.notExists(cardReaderPath) || (Files.notExists(microSdReaderPath))) {
				cardReaderPath = Paths.get(DEV_SDA1);
				microSdReaderPath = Paths.get(DEV_SDB1);
				Thread.sleep(1000L);
				BotLogger.info(TAG, "cardReaderPath.exists= " + (!Files.exists(cardReaderPath)));
				BotLogger.info(TAG, "microSdReaderPath.exists= " + (!Files.exists(microSdReaderPath)));
			}

			Files.createDirectories(Paths.get(HOME_DIR));

			if (Files.exists(cardReaderPath)) {
				output = output + launchBackup(DEV_SDA1, CARD_MOUNT_POINT) + System.lineSeparator();
			}
			if (Files.exists(microSdReaderPath)) {
				output = output + launchBackup(DEV_SDB1, MICROSD_MOUNT_POINT) + System.lineSeparator();
			}

		} catch (Exception e) {
			BotLogger.error(TAG, e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}
		return output;
	}

	private String launchBackup(String device, String mountPoint) throws Exception {

		BotLogger.debug(TAG, "Mounting " + device + " into " + mountPoint);
		Runtime.getRuntime().exec(new String[] { MOUNT_CMD, device, mountPoint }).waitFor();

		String fileIdName = Utils.getFileId(mountPoint);

		String label = Utils.getLabelDevice(device);
		Files.createDirectories(Paths.get(HOME_DIR + File.separator + label));

		String backupPath = HOME_DIR + File.separator + label + File.separator + fileIdName;

		BotLogger.debug(TAG, "launch rsync command");
		RSync rsync = new RSync().source(mountPoint + File.separator).destination(backupPath).archive(true)
				.verbose(true).exclude(new String[] { "*.id", "*.dat", "IndexerVolumeGuid" });
		CollectingProcessOutput output = rsync.execute();

		BotLogger.info(TAG,
				"Backup from " + mountPoint + " finished with exit code = " + String.valueOf(output.getExitCode()));

		if (output.getExitCode() > 0) {
			BotLogger.error(TAG, output.getStdErr());
			return output.getStdErr();
		}

		Runtime.getRuntime().exec(new String[] { "chmod", "-R", "a=rwx", backupPath }).waitFor();

		BotLogger.info(TAG, output.getStdOut());
		return "DEVICE: " + label + System.lineSeparator() + output.getStdOut();
	}
}