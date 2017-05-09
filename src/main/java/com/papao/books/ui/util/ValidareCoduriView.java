package com.papao.books.ui.util;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class ValidareCoduriView extends AbstractCViewAdapter {

	public ValidareCoduriView(final Shell parent) {
		super(parent, AbstractView.MODE_NONE);
		updateDetailMessage("Tester pentru validitatea diferitelor coduri");
		addComponents();
	}

	private void addComponents() {
		new CNPValidator(getContainer(), "CNP", "Cod Numeric Personal", "<Introduceti un CNP>");
		new CIFValidator(
			getContainer(),
			"CIF",
			"Cod de Identificare Fiscala",
			"<Introduceti un CIF>");
		new EuroValidator(
			getContainer(),
			"EURO",
			"Serie bancnota EURO",
			"<Introduceti o serie de bancnota>");
		new CardBancarValidator(
			getContainer(),
			"Card",
			"Numar card bancar",
			"<Introduceti un numar de card>");
		new EAN13Validator(
			getContainer(),
			"EAN13",
			"European Article Number (Cod de Bare)",
			"<Introduceti un cod EAN13 (cod de bare)>");
		new IBANValidator(
			getContainer(),
			"IBAN",
			"International Bank Account Number",
			"<Introduceti un cod IBAN>");
		new ISBNValidator(
			getContainer(),
			"ISBN",
			"International Standard Book Number (din 10 sau 13 cifre)",
			"<Introduceti un cod ISBN>");
	}

	@Override
	public void customizeView() {
		setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);
		setViewOptions(AbstractView.ADD_CANCEL);
		setBigViewImage(AppImages.getImage24(AppImages.IMG_OK));
		setBigViewMessage("Validare coduri");
		setShellText("Validator coduri");
		setShellImage(AppImages.getImage16(AppImages.IMG_OK));
	}

	private class CIFValidator extends AbstractCodValidator {

		public CIFValidator(final Composite parent,
							final String name,
							final String longName,
							final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareCIF(this.text.getText());
		}

	}

	private class CNPValidator extends AbstractCodValidator {

		public CNPValidator(final Composite parent,
							final String name,
							final String longName,
							final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareCNP(this.text.getText());
		}

	}

	private class EuroValidator extends AbstractCodValidator {

		public EuroValidator(	final Composite parent,
								final String name,
								final String longName,
								final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareBancnotaEuro(this.text.getText());
		}

	}

	private class CardBancarValidator extends AbstractCodValidator {

		public CardBancarValidator(	final Composite parent,
									final String name,
									final String longName,
									final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareCardBancar(this.text.getText());
		}

	}

	private class EAN13Validator extends AbstractCodValidator {

		public EAN13Validator(	final Composite parent,
								final String name,
								final String longName,
								final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareEAN13(this.text.getText());
		}

	}

	private class IBANValidator extends AbstractCodValidator {

		public IBANValidator(	final Composite parent,
								final String name,
								final String longName,
								final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validareIBAN(this.text.getText());
		}

	}

	private class ISBNValidator extends AbstractCodValidator {

		public ISBNValidator(	final Composite parent,
								final String name,
								final String longName,
								final String textMessage) {
			super(parent, name, longName, textMessage);
		}

		@Override
		public boolean validare() {
			return ValidareCoduri.validateISBN(this.text.getText());
		}

	}

	private abstract class AbstractCodValidator extends Composite implements Listener {
		private Label labelResultValidare;
		protected Text text;
		private ToolItem itemValidare;
		private Label temp;
		private final String name;
		private final String longName;
		private final String textMessage;

		public AbstractCodValidator(final Composite parent,
									final String name,
									final String longName,
									final String textMessage) {
			super(parent, SWT.NONE);
			this.name = name;
			this.longName = longName;
			this.textMessage = textMessage;
			GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this);
			GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).extendedMargins(10,
					0,
					0,
					0).applyTo(this);

			addComponents();

			WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
		}

		private void addComponents() {
			this.temp = new Label(this, SWT.NONE);
			this.temp.setText(this.name);
			this.temp.setToolTipText(this.longName);
			GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(50, SWT.DEFAULT).applyTo(this.temp);

			this.text = new Text(this, SWT.SEARCH);
			this.text.setMessage(this.textMessage);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(300,
					SWT.DEFAULT).applyTo(this.text);
			this.text.addListener(SWT.Modify, this);
			this.text.addListener(SWT.DefaultSelection, this);

			this.itemValidare = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.NONE);
			GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(this.itemValidare.getParent());
			this.itemValidare.setImage(AppImages.getImage16(AppImages.IMG_HELP));
			this.itemValidare.addListener(SWT.Selection, this);

			this.labelResultValidare = new Label(this, SWT.NONE);
			this.labelResultValidare.setToolTipText("Rezultatul validarii");
			GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(24, 24).applyTo(this.labelResultValidare);
		}

		public abstract boolean validare();

		@Override
		public final void handleEvent(final Event e) {
			if (e.type == SWT.Modify) {
				if (e.widget == this.text) {
					this.labelResultValidare.setImage(null);
				}
			} else if (e.type == SWT.Selection) {
				if (e.widget == this.itemValidare) {
					if (validare()) {
						this.labelResultValidare.setImage(AppImages.getImage16(AppImages.IMG_OK));
						return;
					}
					this.labelResultValidare.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
				}
			} else if (e.type == SWT.DefaultSelection) {
				if (e.widget == this.text) {
					if (validare()) {
						this.labelResultValidare.setImage(AppImages.getImage16(AppImages.IMG_OK));
						return;
					}
					this.labelResultValidare.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
				}
			}
		}

	}

}
