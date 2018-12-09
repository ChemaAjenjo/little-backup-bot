package com.littlebackup.bot;

import static com.littlebackup.utils.Constants.COMMAND_PREFIX;
import static com.littlebackup.utils.Constants.TG_READER_BACKUP_CMD;
import static com.littlebackup.utils.Constants.TG_START_CMD;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.littlebackup.bot.config.LittleBackupBotConfig;
import com.littlebackup.box.commands.ReaderBackupCmd;
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
		try {
			Long chatId = update.getMessage().getChatId();
			this.message.setChatId(chatId);
			this.message.setParseMode("Markdown");
			if ((chatId.equals(this.botConfig.getCreatorId())) && (update.hasMessage())) {
				if (update.getMessage().getText().startsWith(COMMAND_PREFIX)) {

					BotLogger.info(TAG, "Executing " + update.getMessage().getText() + " command");

					switch (update.getMessage().getText().substring(1)) {

					case TG_START_CMD:
						if (Utils.existsFolders()) {
							execute(this.message.setText("Starting Setup"));
							execute(this.message.setText("`" + new SetupCmd().executeCommand() + "`"));
							execute(this.message.setText("Finished setup"));
						} else {
							execute(this.message.setText("Raspberry pi is configured"));
						}
						break;

					case TG_READER_BACKUP_CMD:
						if (!Utils.existsFolders()) {
							execute(this.message.setText("Starting *reader-backup*"));
							execute(this.message.setText("`" + new ReaderBackupCmd().executeCommand() + "`"));
							execute(this.message.setText("Finished *reader-backup*"));
						} else {
							execute(this.message
									.setText("Raspberry pi is not configured, please execute /start command"));
						}
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

}
