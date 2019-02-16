package com.littlebackup.web.model;

import java.io.File;

public class Picture {

	private String path;
	private String name;

	public Picture(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}

	public Picture(File file) {
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
