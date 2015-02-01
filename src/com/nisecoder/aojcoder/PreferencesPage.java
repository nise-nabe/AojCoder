package com.nisecoder.aojcoder;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private StringFieldEditor userIdEditor;
	private StringFieldEditor passwordEditor;

	public PreferencesPage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(AojCoderPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		// add userId form
		userIdEditor = new StringFieldEditor("aojUserId", "user id",
				getFieldEditorParent());
		addField(userIdEditor);

		// add password form
		passwordEditor = new StringFieldEditor("aojPassword", "password", getFieldEditorParent());
		passwordEditor.getTextControl(getFieldEditorParent()).setEchoChar((char) 0x25CF);
		addField(passwordEditor);
	}

}
