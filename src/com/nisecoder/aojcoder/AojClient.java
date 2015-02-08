package com.nisecoder.aojcoder;

import java.net.MalformedURLException;
import java.net.URL;

public class AojClient {
	public static URL getDescriptionURL(String id) {
		try {
			return new URL(
					"http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id="
							+ id);
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

}
