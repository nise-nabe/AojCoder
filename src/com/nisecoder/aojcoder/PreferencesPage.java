package com.nisecoder.aojcoder;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	StringFieldEditor userIdEditor;

	public PreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(AojCoderPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		userIdEditor = new StringFieldEditor("aojUserId", "user id",
				getFieldEditorParent());
		addField(userIdEditor);
	}

}
