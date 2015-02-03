package com.nisecoder.aojcoder;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.FileEditorInput;

public class OpenProjectListener implements SelectionListener, MouseListener {
	Table table;

	public void setTable(Table table) {
		this.table = table;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		openProject();
	}

	private void openProject() {
		TableItem[] items = table.getSelection();
		final String id = items[0].getText();
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			final IProject project = root.getProject(createProjectName(id));
			IFile sourceFile = project.getFile("Main.java");
			final QualifiedName key = new QualifiedName(
					AojCoderPlugin.PLUGIN_ID, "problemId");
			if (project.exists()) {
				if (project.getPersistentProperty(key) == null) {
					project.setPersistentProperty(key, id);
				}
				openEditor(sourceFile);
				return;
			}
			WorkspaceModifyOperation projectCreationOperation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException,
						InterruptedException {
					project.create(monitor);
					project.open(monitor);
					project.setPersistentProperty(key, id);
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
		newProjectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID,
				AojNature.NATURE_ID });
		project.setDescription(newProjectDescription, null);
	}

	private void createNewFile(IFile sourceFile) throws CoreException {
		sourceFile.create(
				new ByteArrayInputStream("public class Main{\n}".getBytes()),
				true, null);
	}

	private void addCompileOption(IJavaProject javaProject) {
		final String JAVA_VERSION = JavaCore.VERSION_1_6;
		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JAVA_VERSION);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JAVA_VERSION);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JAVA_VERSION);
	}

	private void addBuildpath(IJavaProject javaProject)
			throws JavaModelException {
		IClasspathEntry sourceEntry = JavaCore.newSourceEntry(javaProject
				.getPath());
		IClasspathEntry conEntry = JavaCore.newContainerEntry(new Path(
				JavaRuntime.JRE_CONTAINER));
		javaProject.setRawClasspath(new IClasspathEntry[] { sourceEntry,
				conEntry }, null);
	}

	private void openEditor(IFile sourceFile) throws PartInitException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		window.getActivePage().openEditor(new FileEditorInput(sourceFile),
				JavaUI.ID_CU_EDITOR);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		openProject();
	}

	@Override
	public void mouseDown(MouseEvent e) {

	}

	@Override
	public void mouseUp(MouseEvent e) {

	}

}
