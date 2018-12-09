package com.littlebackup.utils;

import java.io.File;

public class Constants {
	public static String HOME_DIR = File.separator + "home" + File.separator + "pi" + File.separator + "BACKUP";
	public static String CARD_DEV = "sda1";
	public static String CARD_MOUNT_POINT = File.separator + "media" + File.separator + "card";
	public static String MICROSD_DEV = "sdb1";
	public static String MICROSD_MOUNT_POINT = File.separator + "media" + File.separator + "microsd";

	public static String DEV = File.separator + "dev";
	public static final String FORMAT_DATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String COMMAND_PREFIX = "/";

	public static final String TG_START_CMD = "start";
	public static final String TG_READER_BACKUP_CMD = "readerbackup";

	public static String BLKID_CMD = "blkid";
}