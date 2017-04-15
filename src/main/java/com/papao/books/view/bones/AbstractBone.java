package com.papao.books.view.bones;

import com.papao.books.FiltruAplicatie;
import com.papao.books.view.AppImages;
import com.papao.books.view.bones.filter.AbstractBoneFilter;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.interfaces.IEncodePrint;
import com.papao.books.view.searcheable.BorgSearchSystem;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.sql.SQLException;

public abstract class AbstractBone extends Composite {

	private static Logger logger = Logger.getLogger(AbstractBone.class);

	private String boneName;
	private int idBone;
	private boolean barOpsShowsText;
	private AbstractBoneDescriptor descriptor;
	private ToolItem itemExport;
	private ToolItem itemPrint;
	private Menu menuExport;
	private Menu menuPrint;
	private AbstractBoneFilter filtru;
	private BorgSearchSystem searchSystem;
	private CWaitDlgClassic waitDlg;
	private Composite group;

	public static final String FILTRARE_DEFAULT = "<filtrare>";

	public AbstractBone(final Composite parent,
						final String name,
						final int idBone,
						final AbstractBoneFilter filtru,
						final AbstractBoneDescriptor descriptor) {
		super(parent, SWT.NONE);
		try {
			setBoneName(name);
			setIdBone(idBone);
			this.filtru = filtru;
			setBarOpsShowingText((filtru != null) && filtru.isBarOpsShowingText());
			setDescriptor(descriptor);
			if (getDescriptor() != null) {
				getDescriptor().setBone(this);
			}
			GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
			GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(0, 0, 0, 0).applyTo(this);
			this.group = new Canvas(this, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(this.group);
			GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 5, 2).applyTo(this.group);
			this.group.addListener(SWT.Paint, new Listener() {

				@Override
				public void handleEvent(final Event e) {
					e.gc.setForeground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
					e.gc.drawRoundRectangle(0,
							0,
							getBounds().width - 1,
							getBounds().height - 1,
							8,
							8);

				}
			});
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			SWTeXtension.displayMessageE("A intervenit o eroare. Pentru a preveni erorile succesive care ar putea sa apara, aplicatia se va inchide acum.",
					"Eroare aplicatie",
					exc);
//			BooksApplication.closeApp(true);
		}
	}

	public void computeTotal() {}

	public Composite getContainer() {
		return this.group;
	}

	public final AbstractBoneFilter getFiltru() {
		return this.filtru;
	}

	public final void initialize() {
		try {
			finishImplementation();
			if (FiltruAplicatie.isAutopopulateTabs()) {
				refresh();
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			SWTeXtension.displayMessageE("A intervenit o eroare la lansarea unei componente. Pentru a preveni "
					+ "erorile succesive care ar putea sa apara, aplicatia se va inchide acum.",
					"Eroare in aplicatie",
					exc);
//			BooksApplication.closeApp(true);
		}
	}

    public abstract void finishImplementation() throws SQLException;

	public abstract void refresh();

	public abstract int getCount();

	protected final void createToolItemExport(final ToolBar bar) {
		setItemExport(new ToolItem(bar, SWT.DROP_DOWN));
		getItemExport().setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
		getItemExport().setHotImage(AppImages.getImage16Focus(AppImages.IMG_EXPORT));
		getItemExport().setToolTipText("Optiuni de export");
		getItemExport().addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				if (getMenuExport() != null) {
					getMenuExport().setVisible(true);
				}
			}
		});
		if (isBarOpsShowingText()) {
			getItemExport().setText("Export");
		}
		getDescriptor().createExportMenu(bar);
	}

	protected final void createToolItemTiparire(final ToolBar bar) {
		if (!(getDescriptor() instanceof IEncodePrint)) {
			logger.warn("Method to create GUI was called by \"" + getBoneName()
					+ ",\" but the caller is not an instance of "
					+ IEncodePrint.class.getCanonicalName() + " . UI is not available.");
			return;
		}
		setItemPrint(new ToolItem(bar, SWT.DROP_DOWN));
		getItemPrint().setImage(AppImages.getImage16(AppImages.IMG_PRINT));
		getItemPrint().setHotImage(AppImages.getImage16Focus(AppImages.IMG_PRINT));
		getItemPrint().setToolTipText("Optiuni tiparire");
		getItemPrint().addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				if (getMenuPrint() != null) {
					getMenuPrint().setVisible(true);
				}
			}
		});
		if (isBarOpsShowingText()) {
			getItemPrint().setText("Tiparire");
		}
		getDescriptor().createPrintMenu(bar);
	}

	public final String getBoneName() {
		return this.boneName;
	}

	public final void setBoneName(final String boneName) {
		this.boneName = boneName;
	}

	public final boolean isBarOpsShowingText() {
		return this.barOpsShowsText;
	}

	private final void setBarOpsShowingText(final boolean barOpsShowsText) {
		this.barOpsShowsText = barOpsShowsText;
	}

	public final int getIdBone() {
		return this.idBone;
	}

	public final void setIdBone(final int idBone) {
		this.idBone = idBone;
	}

	public final BorgSearchSystem getSearchSystem() {
		return this.searchSystem;
	}

	public final void setSearchSystem(final BorgSearchSystem searchSystem) {
		this.searchSystem = searchSystem;
		this.searchSystem.setParentInstance(this);
	}

	public final AbstractBoneDescriptor getDescriptor() {
		return this.descriptor;
	}

	private final void setDescriptor(final AbstractBoneDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public final ToolItem getItemExport() {
		return this.itemExport;
	}

	public final Menu getMenuPrint() {
		return this.menuPrint;
	}

	public final void setMenuPrint(final Menu menuPrint) {
		this.menuPrint = menuPrint;
	}

	protected final void setItemExport(final ToolItem itemExport) {
		this.itemExport = itemExport;
	}

	public final ToolItem getItemPrint() {
		return this.itemPrint;
	}

	protected final void setItemPrint(final ToolItem itemPrint) {
		this.itemPrint = itemPrint;
	}

	public final Menu getMenuExport() {
		return this.menuExport;
	}

	public final void setMenuExport(final Menu menuExport) {
		this.menuExport = menuExport;
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

}
