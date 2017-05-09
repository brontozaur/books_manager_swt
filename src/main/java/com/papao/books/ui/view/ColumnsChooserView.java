package com.papao.books.ui.view;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.interfaces.IReset;
import com.papao.books.ui.util.WidgetCompositeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class ColumnsChooserView extends AbstractCView implements IReset {

	private final ColumnsChooserComposite chooser;

	public ColumnsChooserView(final Tree tree, final Class<?> clazz, final String sufix) {
		super(tree.getShell(), AbstractView.MODE_NONE);
		this.chooser = new ColumnsChooserComposite(getContainer(), tree, clazz, sufix);
		WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
	}

	public ColumnsChooserView(final Table table, final Class<?> clazz, final String sufix) {
		super(table.getShell(), AbstractView.MODE_NONE);
		this.chooser = new ColumnsChooserComposite(getContainer(), table, clazz, sufix);
		WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());
	}

	@Override
	protected void customizeView() {
		setShellStyle(SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		setViewOptions(AbstractView.ADD_OK | AbstractView.ADD_CANCEL);
		setShellImage(AppImages.getImage16(AppImages.IMG_SELECT));
		setShellText("Coloane, dimensiuni si alinieri");
		setBigViewImage(AppImages.getImage24(AppImages.IMG_SELECT));
		setBigViewMessage("Selectati coloanele dorite");
		setShowSaveOKMessage(false);
	}

	@Override
    protected boolean validate() {
		return this.chooser.validate();
	}

	@Override
    protected void saveData() {
        this.chooser.save(true);
	}

	@Override
	public void reset() {
		this.chooser.reset();
	}

}
