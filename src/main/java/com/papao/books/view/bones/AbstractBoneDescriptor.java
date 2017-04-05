package com.papao.books.view.bones;

import com.papao.books.model.AbstractDB;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.filter.AbstractBoneFilter;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.interfaces.*;
import com.papao.books.view.searcheable.BorgSearchSystem;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public abstract class AbstractBoneDescriptor implements Searcheable, IEncodeExport {

	private static Logger logger = Logger.getLogger(AbstractBoneDescriptor.class);

	private ColumnViewer viewer;
	private AbstractBone bone;
	private CWaitDlgClassic waitDlg;

	public final BorgSearchSystem getSearchSystem() {
		return getBone().getSearchSystem();
	}

	public final void createViewer(final Composite comp) {
		if (getBone() instanceof ITreeBone) {
			setViewer(new TreeViewer(comp, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL
					| SWT.BORDER));
		} else if (getBone() instanceof ITableBone) {
			setViewer(new TableViewer(comp, SWT.SINGLE | SWT.FULL_SELECTION | SWT.VIRTUAL
					| SWT.BORDER));
		} else {
			throw new IllegalArgumentException("Cannot create a component with unspecified viewer");
		}
		getViewer().setUseHashlookup(true);
	}

	public abstract void initViewerCols(final AbstractBoneFilter filter);

    public abstract void createViewerFilters(final BorgSearchSystem sys);

	public abstract String[] getTableCols();

	public final ColumnViewer getViewer() {
		return this.viewer;
	}

	public final void setViewer(final ColumnViewer viewer) {
		this.viewer = viewer;
	}

	public final AbstractBone getBone() {
		return this.bone;
	}

	public final void setBone(final AbstractBone bone) {
		this.bone = bone;
	}

	protected void createPrintMenu(final ToolBar bar) {
		MenuItem item;
		if (!(this instanceof IEncodePrint)) {
			logger.warn("Method to create GUI was called by \"" + getBone().getBoneName()
					+ ",\" but the caller is not an instance of "
					+ IEncodePrint.class.getCanonicalName() + " . UI is not available.");
			return;
		}
		if ((bar == null) || bar.isDisposed()) {
			return;
		}
		final Menu menuPrint = new Menu(bar);
		menuPrint.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				boolean isDboValidSelectat = false;
				if (getBone() instanceof ITableBone) {
					Table table = ((ITableBone) getBone()).getViewer().getTable();
					isDboValidSelectat = (table.getSelectionIndex() != -1)
							&& (table.getSelection()[0].getData() instanceof AbstractDB);
				}
				menuPrint.getItem(0).setEnabled(isDboValidSelectat);
				menuPrint.getItem(1).setEnabled(isDboValidSelectat);
			}
		});
		bar.setMenu(menuPrint);

		item = new MenuItem(menuPrint, SWT.NULL);
		item.setText("Tiparire pe ecran");
		item.setImage(AppImages.getImage16(AppImages.IMG_ADOBE));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodePrint) AbstractBoneDescriptor.this).printPDF();
			}
		});

		item = new MenuItem(menuPrint, SWT.NULL);
		item.setText("Tiparire la imprimanta");
		item.setImage(AppImages.getImage16(AppImages.IMG_PRINT));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodePrint) AbstractBoneDescriptor.this).printPrinter();
			}
		});

		getBone().setMenuPrint(menuPrint);
	}

	@Override
	public final void createExportMenu(final Control bar) {
		MenuItem item;
		if ((bar == null) || bar.isDisposed()) {
			return;
		}
		Menu menuExport = new Menu(bar);
		bar.setMenu(menuExport);

		item = new MenuItem(menuExport, SWT.NULL);
		item.setText("Export fisier PDF");
		item.setImage(AppImages.getImage16(AppImages.IMG_ADOBE));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodeExport) AbstractBoneDescriptor.this).exportPDF();
			}
		});

		item = new MenuItem(menuExport, SWT.NULL);
		item.setText("Export fisier Excel");
		item.setImage(AppImages.getImage16(AppImages.IMG_EXCEL));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodeExport) AbstractBoneDescriptor.this).exportExcel();
			}
		});

		item = new MenuItem(menuExport, SWT.NULL);
		item.setText("Export fisier TXT");
		item.setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodeExport) AbstractBoneDescriptor.this).exportTxt();
			}
		});

		item = new MenuItem(menuExport, SWT.NULL);
		item.setText("Export fisier RTF");
		item.setImage(AppImages.getImage16(AppImages.IMG_WORD2));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodeExport) AbstractBoneDescriptor.this).exportRTF();
			}
		});

		item = new MenuItem(menuExport, SWT.NULL);
		item.setText("Export fisier HTML");
		item.setImage(AppImages.getImage16(AppImages.IMG_BROWSER));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				((IEncodeExport) AbstractBoneDescriptor.this).exportHTML();
			}
		});

		getBone().setMenuExport(menuExport);
	}

	private CWaitDlgClassic getWaitDlg(final String message, final boolean open) {
		if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
			this.waitDlg = new CWaitDlgClassic(message);
		}
		if (open) {
			this.waitDlg.open();
		}
		return this.waitDlg;
	}

	protected final void setDlgMessage(final String message) {
		if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
			getWaitDlg(message, true);
			return;
		}
		if (message == null) {
			return;
		}
		this.waitDlg.setMessageLabel(message);
	}

	protected final void closeDlg() {
		if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
			return;
		}
		this.waitDlg.close();
	}

	@Override
	public final void exportExcel() {
//		if (getBone() instanceof ITableBone) {
//			Exporter.export(Exporter.XLS,
//					((TableViewer) getViewer()).getTable(),
//					getBone().getBoneName(),
//					getClass());
//		} else {
//			SWTeXtension.displayMessageW("Aceasta functionalitate nu este inca implementata!");
//		}
	}

	@Override
	public final void exportTxt() {
//		if (getBone() instanceof ITableBone) {
//			Exporter.export(Exporter.TXT,
//					((TableViewer) getViewer()).getTable(),
//					getBone().getBoneName(),
//					getClass());
//		} else {
//			SWTeXtension.displayMessageW("Aceasta functionalitate nu este inca implementata!");
//		}
	}

	@Override
	public final void exportHTML() {
//		if (getBone() instanceof ITableBone) {
//			Exporter.export(Exporter.HTML,
//					((TableViewer) getViewer()).getTable(),
//					getBone().getBoneName(),
//					getClass());
//		} else {
//			SWTeXtension.displayMessageW("Aceasta functionalitate nu este inca implementata!");
//		}
	}

	@Override
	public final void exportPDF() {
//		if (getBone() instanceof ITableBone) {
//			Exporter.export(Exporter.PDF,
//					((TableViewer) getViewer()).getTable(),
//					getBone().getBoneName(),
//					getClass());
//		} else {
//			SWTeXtension.displayMessageW("Aceasta functionalitate nu este inca implementata!");
//		}
	}

	@Override
	public final void exportRTF() {
//		if (getBone() instanceof ITableBone) {
//			Exporter.export(Exporter.RTF,
//					((TableViewer) getViewer()).getTable(),
//					getBone().getBoneName(),
//					getClass());
//		} else {
//			SWTeXtension.displayMessageW("Aceasta functionalitate nu este inca implementata!");
//		}
	}
}
