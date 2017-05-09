package com.papao.books.ui.view;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class InfoView extends AbstractCViewAdapter {

	private final String[] colNames;
	private final String[] colDescriptions;

	public InfoView(final Shell parent, final String[] colNames, final String[] colDescriptions) {
		super(parent, AbstractView.MODE_NONE);
		getShell().setText("Informatii fisier preluare");
		getShell().setImage(AppImages.getImage16(AppImages.IMG_INFO));
		this.colNames = colNames.clone();
		this.colDescriptions = colDescriptions.clone();
		addComponents();
	}

	private void addComponents() {
		Table table;
		TableColumn colNrCrt;
		TableColumn colName;
		TableColumn colDesc;
		TableItem item;

		table = new Table(getContainer(), SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).hint(360, 300).applyTo(table);
		colNrCrt = new TableColumn(table, SWT.NONE);
		colNrCrt.setWidth(60);
		colNrCrt.setText("Coloana");
		colName = new TableColumn(table, SWT.NONE);
		colName.setWidth(100);
		colName.setText("Proprietate");
		colDesc = new TableColumn(table, SWT.NONE);
		colDesc.setWidth(200);
		colDesc.setText("Descriere");

		for (int i = 0; i < this.colNames.length; i++) {
			item = new TableItem(table, SWT.NONE);
			item.setText(0, String.valueOf(i + 1));
			item.setText(1, this.colNames[i]);
			item.setText(2, this.colDescriptions[i]);
		}

		table.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				Table t = (Table) e.widget;
				if ((t == null) || t.isDisposed() || (t.getSelectionIndex() == -1)) {
					return;
				}
				StringBuilder str = new StringBuilder();
				for (int i = 0; i < t.getColumnCount(); i++) {
					if (i > 0) {
						str.append("#");
					}
					str.append(t.getItem(t.getSelectionIndex()).getText(i));
				}
				updateDetailMessage(str.toString());
			}
		});

		table.setMenu(new Menu(table));
		final MenuItem it = new MenuItem(table.getMenu(), SWT.PUSH);
		it.setText("Inchide fereastra");
		it.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				saveAndClose(true);
			}
		});
		WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
		WidgetCursorUtil.addHandCursorListener(table);
	}

	@Override
	public void customizeView() {
		setBigViewImage(AppImages.getImage24(AppImages.IMG_INFO));
		setBigViewMessage("Descriere campuri fisier preluare");
		setViewOptions(AbstractView.ADD_CANCEL);
	}

}
