package com.littlebackup.bot;

import static com.littlebackup.utils.Constants.COMMAND_PREFIX;
import static com.littlebackup.utils.Constants.MAX_LENGTH_TG_MESSAGE;
import static com.littlebackup.utils.Constants.TG_CARD_BACKUP_CMD;
import static com.littlebackup.utils.Constants.TG_DEVICE_BACKUP_CMD;
import static com.littlebackup.utils.Constants.TG_POWEROFF_CMD;
import static com.littlebackup.utils.Constants.TG_READER_BACKUP_CMD;
import static com.littlebackup.utils.Constants.TG_REBOOT_CMD;
import static com.littlebackup.utils.Constants.TG_START_CMD;

import java.io.IOException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.littlebackup.bot.config.LittleBackupBotConfig;
import com.littlebackup.box.commands.CardBackupCmd;
import com.littlebackup.box.commands.DeviceBackupCmd;
import com.littlebackup.box.commands.PowerOffCmd;
import com.littlebackup.box.commands.ReaderBackupCmd;
import com.littlebackup.box.commands.RebootCmd;
import com.littlebackup.box.commands.SetupCmd;
import com.littlebackup.utils.Utils;

public class LittleBackupBot extends TelegramLongPollingBot {

	public static final String TAG = LittleBackupBot.class.getSimpleName();

	private LittleBackupBotConfig botConfig;
	private SendMessage message = new SendMessage();

	public LittleBackupBot(LittleBackupBotConfig botConfig) {
		this.botConfig = botConfig;
	}

	public String getBotUsername() {
		return this.botConfig.getBotUserName();
	}

	public String getBotToken() {
		return this.botConfig.getBotToken();
	}

	public void onUpdateReceived(Update update) {
		String outputCmd = "";
		try {
			Long chatId = update.getMessage().getChatId();
			this.message.setChatId(chatId);
			this.message.setParseMode(ParseMode.MARKDOWN);
			if ((chatId.equals(this.botConfig.getCreatorId())) && (update.hasMessage())) {
				if (update.getMessage().getText().startsWith(COMMAND_PREFIX)) {

					BotLogger.info(TAG, "Executing " + update.getMessage().getText() + " command");

					switch (update.getMessage().getText().substring(1)) {

					case TG_START_CMD:
						if (Utils.existsFolders()) {
							execute(this.message.setText("Starting Setup"));
							execute(this.message.setText("`" + new SetupCmd().execute(this, chatId) + "`"));
							execute(this.message.setText("Finished setup"));
						} else {
							execute(this.message.setText("Raspberry pi is configured"));
						}
						break;

					case TG_READER_BACKUP_CMD:
						if (!Utils.existsFolders()) {
							execute(this.message.setText("Starting *reader-backup*"));
							sendLogBackup(new ReaderBackupCmd().execute(this, chatId), TG_READER_BACKUP_CMD, chatId);
							execute(this.message.setText("Finished *reader-backup*"));
						} else {
							execute(this.message
									.setText("Raspberry pi is not configured, please execute /start command"));
						}
						break;

					case TG_CARD_BACKUP_CMD:
						if (!Utils.existsFolders()) {
							execute(this.message.setText("Starting *card-backup*"));
							sendLogBackup(new CardBackupCmd().execute(this, chatId), TG_CARD_BACKUP_CMD, chatId);
							execute(this.message.setText("Finished *card-backup*"));
						} else {
							execute(this.message
									.setText("Raspberry pi is not configured, please execute /start command"));
						}
						break;

					case TG_DEVICE_BACKUP_CMD:
						if (!Utils.existsFolders()) {
							execute(this.message.setText("Starting *device-backup*"));
							sendLogBackup(new DeviceBackupCmd().execute(this, chatId), TG_DEVICE_BACKUP_CMD, chatId);
							execute(this.message.setText("Finished *device-backup*"));
						} else {
							execute(this.message
									.setText("Raspberry pi is not configured, please execute /start command"));
						}
						break;

					case TG_REBOOT_CMD:
						new RebootCmd().execute(this, chatId);
						break;

					case TG_POWEROFF_CMD:
						new PowerOffCmd().execute(this, chatId);
						break;
					}

				}
			} else {
				execute(new SendMessage().setChatId(chatId).setText("Sorry, this is a private Telegram"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			BotLogger.error(TAG, e.getMessage());
		}
	}

	private void sendLogBackup(String content, String fileName, Long chatId) throws TelegramApiException, IOException {
		if (content.length() > MAX_LENGTH_TG_MESSAGE) {
			execute(this.message.setText("Please download file for watch backup result..."));
			execute(new SendDocument().setChatId(chatId)
					.setDocument(Utils.createBackupLogFile(fileName + ".log", content)));
		} else {
			execute(this.message.setText("`" + content + "`"));
		}
	}

}
