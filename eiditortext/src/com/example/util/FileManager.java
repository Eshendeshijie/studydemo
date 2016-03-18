package com.example.util;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + "com.example/files/";
		} else {
			return CommonUtil.getRootFilePath() + "com.example/files/";
		}
	}
}
