package com.littlebackup.web.model;

import java.io.File;

public class PictureFile {

	private String path;
	private String name;

	public PictureFile(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	public PictureFile(File file) {
		super();
		this.path = file.getAbsolutePath();
		this.name = file.getName();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
