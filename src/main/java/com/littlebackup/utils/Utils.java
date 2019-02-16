package com.littlebackup.utils;

import static com.littlebackup.utils.Constants.BLKID_CMD;
import static com.littlebackup.utils.Constants.FORMAT_DATE_YYYYMMDDHHMMSS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.logging.BotLogger;

import com.littlebackup.box.config.FolderConfig;
import com.littlebackup.web.model.PictureFile;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	public static String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat(FORMAT_DATE_YYYYMMDDHHMMSS);
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	public static boolean existsFolders() {
		return (!Files.exists(Paths.get(FolderConfig.MOUNT_POINT_CARD)))
				|| (!Files.exists(Paths.get(FolderConfig.MOUNT_POINT_MICROSD)))
				|| (!Files.exists(Paths.get(FolderConfig.HOME_DIR)));
	}

	public static String getFileId(String mountPoint) throws java.io.IOException {

		BotLogger.debug(TAG, "Getting fileId from: " + mountPoint);

		String fileId = "";

		File[] idFilesList = new File(mountPoint).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".id");
			}
		});

		if (idFilesList.length == 0) {
			File idFile = new File(mountPoint + File.separator + getCurrentTimeStamp() + ".id");
			idFile.getParentFile().mkdirs();
			idFile.createNewFile();
		} else {
			fileId = idFilesList[(idFilesList.length - 1)].getName().substring(0,
					idFilesList[(idFilesList.length - 1)].getName().indexOf("."));
		}

		BotLogger.debug(TAG, "fileId= " + fileId);

		return fileId;
	}

	public static String getLabelDevice(String device) throws IOException, InterruptedException {
		BotLogger.debug(TAG, "Getting label from device: " + device);
		String label = "";

		ProcessBuilder builder = new ProcessBuilder(new String[0]);
		builder.command(Arrays.asList(BLKID_CMD));
		Process process = builder.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line = "";

		while (((line = reader.readLine()) != null) && (label.isEmpty())) {
			if (line.contains(device)) {
				label = line.substring(line.indexOf("\"") + 1, line.indexOf("\" "));

			}
		}
		int exitCode = process.waitFor();
		assert (exitCode == 0);

		BotLogger.debug(TAG, "label= " + label);

		return label;
	}

	public static File createBackupLogFile(String process, String content) throws IOException {
		File backupLogFile = new File("tmp" + File.separator + process);
		FileUtils.writeStringToFile(backupLogFile, content, Charset.defaultCharset());
		return backupLogFile;
	}

	public static ArrayList<PictureFile> getFileListing(String directoryName) {
		File[] files = new File(directoryName).listFiles();

		ArrayList<PictureFile> filPaths = new ArrayList<PictureFile>();
		for (File file : files) {
			if (file.isFile()) {
				filPaths.add(new PictureFile(file.getAbsolutePath(), file.getName()));
			} else if (file.isDirectory()) {
				for (PictureFile file2 : getFileListing(file.getAbsolutePath())) {
					filPaths.add(file2);
				}
			}
		}
		return filPaths;
	}

	public static HashMap<String, String> getHostData() {

		HashMap<String, String> dataMap = new HashMap<String, String>();
		try {
			InetAddress ip = InetAddress.getLocalHost();
			dataMap.put("hostname", ip.getHostName());
			dataMap.put("ip", ip.getHostAddress());

		} catch (UnknownHostException e) {
			e.printStackTrace();
			BotLogger.error(TAG, e.getCause());
		}

		return dataMap;
	}
}