package com.papao.books.ui.bones.impl.filters;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.bones.filter.AbstractBoneFilter;
import com.papao.books.ui.interfaces.IEncodeHelp;
import com.papao.books.ui.interfaces.IEncodeReset;
import com.papao.books.ui.util.WidgetCompositeUtil;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractGView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AbstractFilterViewMode extends AbstractGView implements IEncodeReset,
		IEncodeHelp {

	private static Logger logger = Logger.getLogger(AbstractFilterViewMode.class);

	public final static int AFISARE_DUPA_DATA = 0;

	private AbstractBoneFilter filtru;
	private ToolBar barSelections;
	private Button buttonItemCount;

	public static final String[] STIL_AFISARE_DATA = new String[] {
			"01,02 ...", "Ian, Feb ...", "Ianuarie, Februarie ..." };

	public static final int AFISARE_TIP_NUMERIC = 0;
	public static final int AFISARE_TIP_NUME_SCURT = 1;
	public static final int AFISARE_FULL = 2;

	public AbstractFilterViewMode(final Shell shell, final AbstractBoneFilter filtru) {
		super(shell, AbstractView.MODE_NONE);

		if (filtru == null) {
			throw new IllegalArgumentException("The filter param should be a cached instance of "
					+ AbstractBoneFilter.class.getCanonicalName() + ". The current filter is null");
		}
		setFiltru(filtru);

		getShell().setText("Tip vizualizare");
		setWidgetLayout(new GridLayout(2, false));
		getContainer().setLayout(getWidgetLayout());
		getContainer().setText("Optiuni vizualizare");

		addComponents();

		SWTeXtension.processToolBarItems(getBarSelections());

		populateFields();
	}

	public abstract void addSpecificDetails();

	public abstract void populateSpecificDetails();

	public abstract void createBarOpsItems();

	public abstract String[] getCriteriiAfisare();

	private void addComponents() {
		GridData gd;
		Label separator;

		this.barSelections = new ToolBar(getContainer(), SWT.FLAT | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = getWidgetLayout().numColumns;
		this.barSelections.setLayoutData(gd);
		WidgetCursorUtil.addHandCursorListener(this.barSelections);

		createBarOpsItems();

		separator = new Label(getContainer(), SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = getWidgetLayout().numColumns;
		separator.setLayoutData(gd);

		addSpecificDetails();

		this.buttonItemCount = new Button(getContainer(), SWT.CHECK);
		this.buttonItemCount.setText("afiseaza numarul de elemente");
		this.buttonItemCount.setToolTipText("Selectand aceasta optiune, se va afisa numarul de inregistrari din tabela, in partea stanga, pe arbore");
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = getWidgetLayout().numColumns;
		this.buttonItemCount.setLayoutData(gd);
		WidgetCursorUtil.addHandCursorListener(this.buttonItemCount);

		WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
	}

	public final void populateFields() {
		for (ToolItem it : this.barSelections.getItems()) {
			if (((Integer) it.getData()).intValue() == getSelectionIndex()) {
				it.setSelection(true);
				continue;
			}
			it.setSelection(false);
		}
		this.buttonItemCount.setSelection(getFiltru().isTreeShowingElementCount());

		populateSpecificDetails();
	}

	@Override
	public final void reset() {
		getFiltru().resetViewMode();
		populateFields();

	}

	@Override
    protected final boolean validate() {
		return true;
	}

	@Override
	public final void customizeView() {
		setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.RESIZE);
		setViewOptions(AbstractView.ADD_OK | AbstractView.ADD_CANCEL);
		setShellImage(AppImages.getImage16(AppImages.IMG_CONFIG));
		setBigViewImage(AppImages.getImage24(AppImages.IMG_CONFIG));
		setBigViewMessage("Selectati modul de afisare");
		setShowSaveOKMessage(false);
	}

	public final int getSelectionIndex() {
		return this.filtru.getTreeViewMode();
	}

	public final AbstractBoneFilter getFiltru() {
		return this.filtru;
	}

	public final void setFiltru(final AbstractBoneFilter filtru) {
		this.filtru = filtru;
	}

	public String getSelectionTypeName() {
		try {
			return getCriteriiAfisare()[getSelectionIndex()];
		}
		catch (ArrayIndexOutOfBoundsException exc) {
			logger.error(exc.getMessage(), exc);
			return getCriteriiAfisare()[0];
		}
	}

	public final ToolBar getBarSelections() {
		return this.barSelections;
	}

	public final void setBarSelections(final ToolBar barSelections) {
		this.barSelections = barSelections;
	}

	public final Button getButtonItemCount() {
		return this.buttonItemCount;
	}

	public final void setButtonItemCount(final Button buttonItemCount) {
		this.buttonItemCount = buttonItemCount;
	}
}
