package com.nisecoder.aojcoder.popup.actions;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.nisecoder.aojcoder.AojCoderPlugin;

public class SubmitAction implements IObjectActionDelegate {

	private Shell shell;
	ICompilationUnit javaFile;

	/**
	 * Constructor for SubmitAction
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
		if (!javaFile.getElementName().equals("Main.java")) {
			MessageDialog.openWarning(shell, "AojCoderPlugin",
					"File Name must be \"Main.java\".");
			return;
		}
		IPreferenceStore pref = AojCoderPlugin.getDefault()
				.getPreferenceStore();
		String userId = pref.getString("aojUserId");
		String password = pref.getString("aojPassword");

		CloseableHttpAsyncClient httpclient;
		try {
			QualifiedName key = new QualifiedName(AojCoderPlugin.PLUGIN_ID,
					"problemId");
			IProject project = javaFile.getJavaProject().getProject();
			String problemId = project.getPersistentProperty(key);

			httpclient = AojCoderPlugin.getDefault().getHttpClient();
			httpclient.start();
			HttpPost post = new HttpPost(
					"http://judge.u-aizu.ac.jp/onlinejudge/servlet/Submit");
			List<BasicNameValuePair> list = Arrays.asList(
					new BasicNameValuePair("userID", userId),
					new BasicNameValuePair("password", password),
					new BasicNameValuePair("problemNO", problemId),
					new BasicNameValuePair("language", "JAVA"),
					new BasicNameValuePair("sourceCode", javaFile.getSource()));
			post.setEntity(new UrlEncodedFormEntity(list));
			httpclient.execute(post, new FutureCallback<HttpResponse>() {

				@Override
				public void cancelled() {
					// TODO Auto-generated method stub

				}

				@Override
				public void completed(HttpResponse response) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(shell,
									"AojCoderPlugin", "Submit was executed.");
						}
					});
				}

				@Override
				public void failed(Exception e) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openInformation(shell,
									"AojCoderPlugin", "Submit was failed.");
						}
					});
				}

			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TreeSelection) {
			Object firstElement = ((TreeSelection) selection).getFirstElement();
			if (firstElement instanceof ICompilationUnit) {
				this.javaFile = (ICompilationUnit) firstElement;
			}
		}
	}
}
