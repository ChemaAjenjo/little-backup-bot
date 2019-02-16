package com.littlebackup.box.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FolderConfig {

	public static String HOME_DIR;

	@Value("${folder.home}")
	public void setHomeDir(String homeDir) {
		HOME_DIR = homeDir;
	}

	public static String DEV_SDA1;

	@Value("${folder.dev.sda1}")
	public void setDevSda1(String devSda1) {
		DEV_SDA1 = devSda1;
	}

	public static String DEV_SDB1;

	@Value("${folder.dev.sdb1}")
	public void setDevSdb1(String devSdb1) {
		DEV_SDB1 = devSdb1;
	}

	public static String MOUNT_POINT_CARD;

	@Value("${folder.mountPoint.card}")
	public void setMountPointCard(String mountPointCard) {
		MOUNT_POINT_CARD = mountPointCard;
	}

	public static String MOUNT_POINT_MICROSD;

	@Value("${folder.mountPoint.microsd}")
	public void setMountPointMicroSD(String mountPointMicroSD) {
		MOUNT_POINT_MICROSD = mountPointMicroSD;
	}

	public static String MOUNT_POINT_STORAGE;

	@Value("${folder.mountPoint.storage}")
	public void setMountPointStorage(String mountPointStorage) {
		MOUNT_POINT_STORAGE = mountPointStorage;
	}
}
