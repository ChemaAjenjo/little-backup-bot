package com.littlebackup.box.commands;

import static com.littlebackup.utils.Constants.DEV_SDA1;
import static com.littlebackup.utils.Constants.DEV_SDB1;
import static com.littlebackup.utils.Constants.HOME_DIR;

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
	public String executeCommand(TelegramLongPollingBot bot, Long chatId) {

		String output = "backup finished";

		try {
			Path storagePath = Paths.get(DEV_SDA1);
			Path cardPath = Paths.get(DEV_SDB1);

			bot.execute(new SendMessage().setChatId(chatId).setText("Please, insert *storage device*"));
			while (!Files.exists(storagePath)) {
				storagePath = Paths.get(DEV_SDA1);
				Thread.sleep(1000L);
			}

			bot.execute(new SendMessage().setChatId(chatId).setText("Please, insert *card*"));
			while (!Files.exists(cardPath)) {
				cardPath = Paths.get(DEV_SDB1);
				Thread.sleep(1000L);
			}

		} catch (Exception e) {
			e.printStackTrace();
			BotLogger.error(TAG, e.getMessage());
			return e.getMessage();
		}
		return output;
	}

	private String launchBackup(String device, String mountPoint) throws Exception {

		BotLogger.debug(TAG, "Mounting " + device + " into " + mountPoint);
		Runtime.getRuntime().exec(new String[] { "mount", device, mountPoint }).waitFor();

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