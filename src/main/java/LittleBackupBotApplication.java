
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.littlebackup.bot.LittleBackupBot;
import com.littlebackup.bot.config.LittleBackupBotConfig;

@SpringBootApplication
@ComponentScan({ "com.littlebackup" })
public class LittleBackupBotApplication implements CommandLineRunner {

	private static final String TAG = LittleBackupBotApplication.class.getCanonicalName();

	private LittleBackupBotConfig botConfig;

	@Autowired
	public void setBotConfig(LittleBackupBotConfig botConfig) {
		this.botConfig = botConfig;
		BotLogger.config(TAG, botConfig.toString());
	}

	public void run(String... args) throws Exception {
		BotLogger.setLevel(Level.ALL);
		BotLogger.info(TAG, "Starting bot");

		TelegramBotsApi api = new TelegramBotsApi();
		try {
			api.registerBot(new LittleBackupBot(this.botConfig));

			BotLogger.info(TAG, "Bot running...");
		} catch (TelegramApiException e) {
			BotLogger.error(TAG, e.getCause());
		}
	}

	public static void main(String[] args) {
		ApiContextInitializer.init();

		SpringApplication.run(LittleBackupBotApplication.class, args);
	}
}
