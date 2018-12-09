package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.CARD_DEV;
import static com.littlebackup.utils.Constants.CARD_MOUNT_POINT;
import static com.littlebackup.utils.Constants.DEV;
import static com.littlebackup.utils.Constants.HOME_DIR;
import static com.littlebackup.utils.Constants.MICROSD_DEV;
import static com.littlebackup.utils.Constants.MICROSD_MOUNT_POINT;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.telegram.telegrambots.meta.logging.BotLogger;

import com.github.fracpete.processoutput4j.output.CollectingProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.littlebackup.utils.Utils;

public class ReaderBackupCmd implements Command {

	public static final String TAG = ReaderBackupCmd.class.getSimpleName();

	public String executeCommand() {

		String output = "";

		try {
			Path cardReaderPath = Paths.get(DEV + File.separator + CARD_DEV);
			Path microSdReaderPath = Paths.get(DEV + File.separator + MICROSD_DEV);

			while ((!Files.exists(cardReaderPath)) || (!Files.exists(microSdReaderPath))) {
				cardReaderPath = Paths.get(DEV + File.separator + CARD_DEV);
				microSdReaderPath = Paths.get(DEV + File.separator + MICROSD_DEV);
				Thread.sleep(1000L);
				BotLogger.info(TAG, "cardReaderPath.exists= " + (!Files.exists(cardReaderPath)));
				BotLogger.info(TAG, "microSdReaderPath.exists= " + (!Files.exists(microSdReaderPath)));
			}

			Files.createDirectories(Paths.get(HOME_DIR));

			if (Files.exists(cardReaderPath)) {
				output = output + launchBackup(CARD_DEV, CARD_MOUNT_POINT) + System.lineSeparator();
			}
			if (Files.exists(microSdReaderPath)) {
				output = output + launchBackup(MICROSD_DEV, MICROSD_MOUNT_POINT) + System.lineSeparator();
			}

		} catch (Exception e) {
			e.printStackTrace();
			BotLogger.error(TAG, e.getMessage());
			return e.getMessage();
		}
		return output;
	}

	private String launchBackup(String device, String mountPoint) throws Exception {

		BotLogger.debug(TAG, "Mounting " + DEV + File.separator + device + " into " + mountPoint);
		Runtime.getRuntime().exec(new String[] { "mount", DEV + File.separator + device, mountPoint }).waitFor();

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