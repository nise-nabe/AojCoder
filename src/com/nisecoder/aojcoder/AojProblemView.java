package com.nisecoder.aojcoder;

import java.awt.Desktop;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.ui.part.ViewPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class AojProblemView extends ViewPart {

	Table table;
	TableViewer viewer;

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
		OpenProjectListener openProject = new OpenProjectListener();
		openProject.setTable(table);
		table.addMouseListener(openProject);
		menuItem.addSelectionListener(openProject);
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
		List<String[]> list = new ArrayList<>();
		try {
			for (String volume : AojConstraints.volumnList) {
				Document problemListXml = Jsoup.parse(new URL(
						AojConstraints.apiEntryPoint + "/problem_list?volume="
								+ volume), 30000);
				Document solvedRecordXml = Jsoup.parse(new URL(
						AojConstraints.apiEntryPoint + "/solved_record?user_id="
								+ userId), 30000);
				Set<String> set = new HashSet<>();
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
