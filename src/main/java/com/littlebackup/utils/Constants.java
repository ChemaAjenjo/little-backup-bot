package com.littlebackup.utils;

import java.io.File;

public class Constants {
	public static String HOME_DIR = File.separator + "home" + File.separator + "pi" + File.separator + "BACKUP";
	public static String DEV_SDA1 = File.separator + "dev" + File.separator + "sda1";
	public static String CARD_MOUNT_POINT = File.separator + "media" + File.separator + "card";
	public static String DEV_SDB1 = File.separator + "dev" + File.separator + "sdb1";
	public static String MICROSD_MOUNT_POINT = File.separator + "media" + File.separator + "microsd";
	public static String STORAGE_MOUNT_POINT = File.separator + "media" + File.separator + "storage";

	// public static String DEV = File.separator + "dev";

	public static final String FORMAT_DATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String COMMAND_PREFIX = "/";

	public static final String TG_START_CMD = "start";
	public static final String TG_READER_BACKUP_CMD = "readerbackup";
	public static final String TG_CARD_BACKUP_CMD = "cardbackup";
	public static final String TG_REBOOT_CMD = "reboot";
	public static final String TG_POWEROFF_CMD = "poweroff";

	public static String BLKID_CMD = "blkid";
	public static String MOUNT_CMD = "mount";
}