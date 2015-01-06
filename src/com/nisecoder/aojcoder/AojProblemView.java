package com.nisecoder.aojcoder;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class AojProblemView extends ViewPart {

	Table table;
	TableViewer viewer;
	String apiEntryPoint = "http://judge.u-aizu.ac.jp/onlinejudge/webservice/";

	@Override
	public void createPartControl(Composite parent) {
		table = new Table(parent, SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ProblemLabelProvider());
		viewer.setContentProvider(new ArrayContentProvider());

		TableColumn tc = new TableColumn(table, SWT.NONE, 0);
		tc.setText("id");
		tc = new TableColumn(table, SWT.NONE, 1);
		tc.setText("name");
		tc = new TableColumn(table, SWT.NONE, 2);
		tc.setText("solved");

		String userId = AojCoderPlugin.getDefault().getPreferenceStore()
				.getString("aojUserId");

		updateList(userId);
		AojCoderPlugin.getDefault().getPreferenceStore()
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent event) {
						if ("aojUserId".equals(event.getProperty())) {
							String userId = event.getNewValue().toString();
							updateList(userId);
						}
					}
				});

		Menu menu = new Menu(parent);
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("open project");
		menuItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getSelection();
				String id = items[0].getText();
				try {
					IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
							.getRoot();
					final IProject project = root
							.getProject(createProjectName(id));
					IFile sourceFile = project.getFile("Main.java");
					if (project.exists()) {
						openEditor(sourceFile);
						return;
					}
					WorkspaceModifyOperation projectCreationOperation = new WorkspaceModifyOperation() {
						@Override
						protected void execute(IProgressMonitor monitor)
								throws CoreException,
								InvocationTargetException, InterruptedException {
							project.create(null);
							project.open(null);
						}
					};
					projectCreationOperation.run(null);

					addDescription(project);
					IJavaProject javaProject = JavaCore.create(project);

					addBuildpath(javaProject);
					addCompileOption(javaProject);
					createNewFile(sourceFile);
					openEditor(sourceFile);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			private String createProjectName(String id) {
				return "AOJ-" + id + "-java";
			}

			private void addDescription(IProject project) throws CoreException {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProjectDescription newProjectDescription = workspace
						.newProjectDescription(project.getName());
				newProjectDescription
						.setNatureIds(new String[] { JavaCore.NATURE_ID });
				project.setDescription(newProjectDescription, null);
			}

			private void createNewFile(IFile sourceFile) throws CoreException {
				sourceFile.create(new ByteArrayInputStream(
						"public class Main{\n}".getBytes()), true, null);
			}

			private void addCompileOption(IJavaProject javaProject) {
				final String JAVA_VERSION = JavaCore.VERSION_1_6;
				javaProject.setOption(JavaCore.COMPILER_COMPLIANCE,
						JAVA_VERSION);
				javaProject.setOption(JavaCore.COMPILER_SOURCE, JAVA_VERSION);
				javaProject
						.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
								JAVA_VERSION);
			}

			private void addBuildpath(IJavaProject javaProject)
					throws JavaModelException {
				IClasspathEntry sourceEntry = JavaCore
						.newSourceEntry(javaProject.getPath());
				IClasspathEntry conEntry = JavaCore.newContainerEntry(new Path(
						JavaRuntime.JRE_CONTAINER));
				javaProject.setRawClasspath(new IClasspathEntry[] {
						sourceEntry, conEntry }, null);
			}

			private void openEditor(IFile sourceFile) throws PartInitException {
				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				window.getActivePage().openEditor(
						new FileEditorInput(sourceFile), JavaUI.ID_CU_EDITOR);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		MenuItem openItem = new MenuItem(menu, SWT.NONE);
		openItem.setText("open page in browser");
		openItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getSelection();
				String id = items[0].getText();
				try {
					URL url = new URL("http://judge.u-aizu.ac.jp/onlinejudge/description.jsp?id="+id);
					Desktop.getDesktop().browse(url.toURI());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// TODO Auto-generated method stub
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		table.setMenu(menu);
	}

	private List<String[]> getProblemList(String userId) {
		List<String[]> list = new ArrayList<String[]>();
		try {
			for (String volume : new String[] { "100", "0", "1", "2", "3", "5",
					"6", "10", "11", "12", "13", "15", "20", "21", "22", "23",
					"24", "25" }) {
				Document problemListXml = Jsoup.parse(new URL(apiEntryPoint
						+ "problem_list?volume=" + volume), 30000);
				Document solvedRecordXml = Jsoup.parse(new URL(apiEntryPoint
						+ "solved_record?user_id=" + userId), 30000);
				Set<String> set = new HashSet<String>();
				for (Element problemId : solvedRecordXml
						.getElementsByTag("problem_id")) {
					set.add(problemId.text());
				}
				for (Element problem : problemListXml
						.getElementsByTag("problem")) {
					String id = problem.getElementsByTag("id").text();
					String name = problem.getElementsByTag("name").text();
					String solved = set.contains(id) ? "o" : "";
					String[] content = new String[] { id, name, solved };
					list.add(content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void setFocus() {
		table.setFocus();
	}

	private void updateList(String userId) {
		viewer.setInput(getProblemList(userId));
		for (int i = 0; i < table.getColumnCount(); ++i) {
			table.getColumn(i).pack();
		}
	}

	private class ProblemLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			String[] strs = (String[]) element;
			return strs[columnIndex];
		}
	}
}
