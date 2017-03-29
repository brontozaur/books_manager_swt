package com.papao.books.ui.custom;

import com.papao.books.ui.AppImages;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public class AdbSelectorComposite extends Composite implements Listener {

	private static Logger logger = Logger.getLogger(AdbSelectorComposite.class);

	private final AdbSelectorData data;
	private Text textInfo;
	private ToolItem itemSelectie;

	public AdbSelectorComposite(final Composite parent, final AdbSelectorData data) {
		super(parent, SWT.NONE);
		this.data = data;

		addComponents();
	}

	private void addComponents() {

		if (this.data.isAddLabel()) {
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).hint(150,
					SWT.DEFAULT).applyTo(this);
		} else {
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).hint(150,
					SWT.DEFAULT).applyTo(this);
		}
		GridLayoutFactory.fillDefaults().numColumns(this.data.isAddLabel() ? 3 : 2).equalWidth(false).margins(0,
				0).spacing(0, 0).extendedMargins(5, 5, 0, 0).applyTo(this);

		if (this.data.isAddLabel()) {
			String str = this.data.getLabelName();
			if (str.length() > 12) {
				str = str.substring(0, 11).concat(".");
			} else {
				while (str.length() < 12) {
					str = str.concat(" ");
				}
			}
			Label temp = new Label(this, SWT.NONE);
			temp.setText(str);
			GridDataFactory.fillDefaults().hint(60, SWT.DEFAULT).align(SWT.BEGINNING, SWT.CENTER).applyTo(temp);
			temp.setToolTipText(this.data.getLabelName());
		}

		this.textInfo = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textInfo);
		this.textInfo.addListener(SWT.KeyDown, this);

		this.itemSelectie = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.PUSH);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(this.itemSelectie.getParent());
		this.itemSelectie.setImage(AppImages.getImage16(AppImages.IMG_SELECT));
		this.itemSelectie.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SELECT));
		this.itemSelectie.setToolTipText("Selectie inregistrari");
		this.itemSelectie.addListener(SWT.Selection, this);
	}

	public final void choose() {
		AdbObjectsCheckView view;
		view = new AdbObjectsCheckView(getShell(), this.data, new Rectangle(
			getShell().getLocation().x,
			getShell().getLocation().y,
			getShell().getBounds().width,
			getShell().getBounds().height));
		view.open(false);
		if (view.getUserAction() == SWT.CANCEL) {
			return;
		}
		setTextInfoValue(view.getDataTransport().getSelectionAsText());
		layout();
	}

	public final void setTextInfoValue(final String value) {
		this.textInfo.setText("");
		if (value != null) {
			this.textInfo.setText(value);
		}
	}

	public final void populate() {
	}

	public AdbSelectorData getDataTransport() {
		return this.data;
	}

	public String parse() {
		return "";
	}

	public final void setText(final String text) {
		this.textInfo.setText(text);
	}

	@Override
	public void handleEvent(final Event event) {
		try {
			if (event.type == SWT.Selection) {
				if (event.widget == this.itemSelectie) {
					choose();
				}
			} else if (event.type == SWT.KeyDown) {
				if (event.widget == this.textInfo) {
					if (event.keyCode == SWT.F3) {
						choose();
						notifyListeners(SWT.Selection, new Event());
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	// this overrides the default action for selection behaviour on itemSelectie widget
	public final void replaceSelectionListener(final Listener lis) {
		this.itemSelectie.removeListener(SWT.Selection, this);
		this.itemSelectie.addListener(SWT.Selection, lis);
	}

	public final void replaceKeyDownListener(final Listener lis) {
		this.textInfo.removeListener(SWT.KeyDown, this);
		this.textInfo.addListener(SWT.KeyDown, lis);
	}

	public final Text getTextInfo() {
		return this.textInfo;
	}
}
