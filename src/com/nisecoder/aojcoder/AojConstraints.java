package com.nisecoder.aojcoder;

import java.net.MalformedURLException;
import java.net.URL;

public class AojConstraints {
	public static final String baseUrl = "http://judge.u-aizu.ac.jp/onlinejudge";
	public static final String apiEntryPoint = baseUrl + "/webservice";
	public static final String[] volumeList = { "100", "0", "1", "2", "3", "5",
			"6", "10", "11", "12", "13", "15", "20", "21", "22", "23", "24",
			"25", "26" };

	public static URL getDescriptionURL(String id) {
		try {
			return new URL(AojConstraints.baseUrl + "/description.jsp?id=" + id);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getProblemListURL(String volume) {
		try {
			return new URL(AojConstraints.apiEntryPoint
					+ "/problem_list?volume=" + volume);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getSolvedRecordURL(String userId) {
		try {
			return new URL(AojConstraints.apiEntryPoint
					+ "/solved_record?user_id=" + userId);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URL getStaticURL() {
		try {
			return new URL("http://judge.u-aizu.ac.jp/onlinejudge/status.jsp");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;

	}
}
