package com.papao.books.ui.searcheable;

import com.papao.books.ui.util.WidgetCursorUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CheckBasedSearch extends AbstractSearchType {

	private final Composite compChecks;
	private final Map<Integer, String> mapChecks;

	public CheckBasedSearch(final BorgSearchSystem searchSystem,
							final String colName,
							final Map<Integer, String> mapChecks) {
		super(searchSystem, colName);
		this.mapChecks = mapChecks;

		this.compChecks = new Composite(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(0,
				0).applyTo(this.compChecks);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(this.compChecks);

		for (Iterator<Entry<Integer, String>> it = mapChecks.entrySet().iterator(); it.hasNext();) {
			Entry<Integer, String> entry = it.next();
			int key = entry.getKey();
			String value = entry.getValue();
			final Button buttonCheck = new Button(this.compChecks, SWT.CHECK);
			buttonCheck.setText(value);
			buttonCheck.setData(key);
			WidgetCursorUtil.addHandCursorListener(buttonCheck);
		}

		this.getParent().layout();
		if (this.getParent().getParent() != null) {
			this.getParent().getParent().layout();
		}
	}

	@Override
	public void createContents() {
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(2, 2, 0, 0).spacing(2,
				0).applyTo(this);
	}

	public Map<Integer, String> getSelection() {
		Map<Integer, String> mapSelection;
		if (isDisposed()) {
			return this.mapChecks;
		}
		mapSelection = new HashMap<Integer, String>();
		for (Control c : this.compChecks.getChildren()) {
			Button b = (Button) c;
			if (b.getSelection()) {
				mapSelection.put((Integer) b.getData(), b.getText());
			}
		}
		if (mapSelection.isEmpty()) {
			return this.mapChecks;
		}
		return mapSelection;
	}

	public final boolean containsValue(final int key) {
		return getSelection().get(key) != null;
	}

	@Override
	public boolean isModified() {
		Map<Integer, String> selection = getSelection();
		boolean result = !selection.isEmpty() && (selection.size() != this.mapChecks.size());
		getLabelName().setForeground(result	? AbstractSearchType.FILTRU_ACTIV
											: AbstractSearchType.FILTRU_INACTIV);
		return result;
	}

	@Override
	public boolean compareValues(final Object valueToBeCompared) {
		if (!(valueToBeCompared instanceof Integer)) {
			return false;
		}
		return containsValue((Integer) valueToBeCompared);
	}
}
