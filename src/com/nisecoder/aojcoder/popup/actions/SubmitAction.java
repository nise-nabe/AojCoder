package com.nisecoder.aojcoder.popup.actions;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SubmitAction implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public SubmitAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(3000).setConnectTimeout(3000).build();

			CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
					.setDefaultRequestConfig(requestConfig).build();

			httpclient.start();
			HttpPost post = new HttpPost(
					"http://judge.u-aizu.ac.jp/onlinejudge/servlet/Submit");
			List<BasicNameValuePair> list = Arrays
					.asList(new BasicNameValuePair("userID", "userid"),
							new BasicNameValuePair("password", "password"),
							new BasicNameValuePair("problemNO", "10001"),
							new BasicNameValuePair("language", "JAVA"),
							new BasicNameValuePair("sourceCode",
									"public class Main{}"));
			post.setEntity(new UrlEncodedFormEntity(list));
			httpclient.execute(post, new FutureCallback<HttpResponse>() {

				@Override
				public void cancelled() {
					// TODO Auto-generated method stub

				}

				@Override
				public void completed(HttpResponse response) {
					MessageDialog.openInformation(shell, "AojCoderPlugin", "Submit was executed.");
				}

				@Override
				public void failed(Exception e) {
					MessageDialog.openInformation(shell, "AojCoderPlugin", "Submit was failed.");
				}

			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
