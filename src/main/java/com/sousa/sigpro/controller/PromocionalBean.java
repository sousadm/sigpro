package com.sousa.sigpro.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

@Named
public class PromocionalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> images;

	public PromocionalBean() {
		images = new ArrayList<String>();
		for (int i = 1; i <= 5; i++) {
			images.add("publicidade" + i + ".jpg");
		}
	}

	public List<String> getImages() {
		return images;
	}
}