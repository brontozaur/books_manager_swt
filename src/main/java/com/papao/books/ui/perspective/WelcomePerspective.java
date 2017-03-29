package com.papao.books.ui.perspective;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.util.ColorUtil;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.*;

public final class WelcomePerspective extends Composite {

	private static Logger logger = Logger.getLogger(WelcomePerspective.class);

	private WelcomeStatusLine statusLine;
	private ToolBar barDocking;
	public static WelcomePerspective instance;
	private CTabFolder mainTabFolder;

	public WelcomePerspective() {
		super(EncodePlatform.instance.getAppMainForm(), SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE
				| SWT.EMBEDDED | SWT.NO_FOCUS);
		WelcomePerspective.instance = this;

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(this);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(0, 0).extendedMargins(0,
				0,
				0,
				0).spacing(0, 0).applyTo(this);
		addComponents();
	}

	public void addComponents() {
		Composite upperComposite = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(upperComposite);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(upperComposite);

        this.mainTabFolder = new CTabFolder(upperComposite, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(this.mainTabFolder);
        this.mainTabFolder.setSimple(true);
        this.mainTabFolder.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        this.mainTabFolder.setUnselectedImageVisible(true);
        this.mainTabFolder.setUnselectedCloseVisible(false);
        this.mainTabFolder.setMRUVisible(true);
        this.mainTabFolder.setMinimizeVisible(false);
        this.mainTabFolder.setMaximizeVisible(false);
        this.mainTabFolder.setSelectionBackground(ColorUtil.COLOR_ALBASTRU_DESCHIS_WINDOWS);

        CTabItem booksTabItem = new CTabItem(this.mainTabFolder, SWT.NONE);
        booksTabItem.setText("Carti");
        booksTabItem.setImage(AppImages.getImage32(AppImages.IMG_BANCA));
        booksTabItem.setControl(createBooksGrid());

        this.mainTabFolder.setSelection(0);

        Composite topRight = new Composite(mainTabFolder, SWT.NONE);
        topRight.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(topRight);
        GridDataFactory.fillDefaults().grab(true, true).hint(450, SWT.DEFAULT).align(SWT.END,
                SWT.CENTER).applyTo(topRight);

        createTopRightComponents(topRight);

        this.mainTabFolder.setTopRight(topRight);

        Composite lowerCompBarDocking = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(lowerCompBarDocking);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).extendedMargins(10,
				10,
				0,
				0).spacing(0, 0).applyTo(lowerCompBarDocking);
		lowerCompBarDocking.setBackground(ColorUtil.COLOR_WHITE);
		lowerCompBarDocking.setBackgroundMode(SWT.INHERIT_DEFAULT);

		this.barDocking = new ToolBar(lowerCompBarDocking, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(this.barDocking);
		this.barDocking.setMenu(createBarDockingMenu());

		setStatusLine(new WelcomeStatusLine(this));
		getStatusLine().getLabelNumeModul().setText("Selectati un modul");
	}

	private void createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.WRAP);
        bar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage32(AppImages.IMG_ARROW_RIGHT));
        item.setHotImage(AppImages.getImage32Focus(AppImages.IMG_ARROW_RIGHT));
        item.setToolTipText("Logout");
        item.setText("Logout");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.logout(true);
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage32(AppImages.IMG_STOP));
        item.setHotImage(AppImages.getImage32Focus(AppImages.IMG_STOP));
        item.setToolTipText("Exit");
        item.setText("Exit");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.performShellClose(new Event());
            }
        });
    }

	private Composite createBooksGrid(){
	    Composite composite = new Composite(this.mainTabFolder, SWT.NONE);
	    new Label(composite, SWT.NONE).setText("Grid-ul cu carti");
	    return composite;
    }

	private Menu createBarDockingMenu() {
		final Menu menu = new Menu(this.barDocking);
		menu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				menu.getItem(0).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
				menu.getItem(1).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
				menu.getItem(2).setEnabled(WelcomePerspective.this.barDocking.getItemCount() > 0);
			}
		});

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Inchidere ferestre");
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
					if (it.getData() instanceof AbstractView) {
						((AbstractView) it.getData()).close(SWT.CANCEL);
					}
					it.dispose();
				}
				WelcomePerspective.this.barDocking.layout();
				WelcomePerspective.this.barDocking.getParent().layout();
				WelcomePerspective.this.barDocking.getParent().getParent().layout();
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MAXIMIZE));
		item.setText("Afisare ferestre");
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
					if (it.getData() instanceof AbstractView) {
						((AbstractView) it.getData()).getDockingItem().notifyListeners(SWT.Selection,
								new Event());
					}
				}
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Minimizare ferestre");
		item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MINIMIZE));
		item.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				for (ToolItem it : WelcomePerspective.this.barDocking.getItems()) {
					if (it.getData() instanceof AbstractView) {
						((AbstractView) it.getData()).getShell().setMinimized(true);
					}
				}
			}
		});

		return menu;
	}

	public final static ToolBar getBarDocking() {
		return WelcomePerspective.instance.barDocking;
	}

	public Composite getContent() {
		return this;
	}

	public WelcomeStatusLine getStatusLine() {
		return this.statusLine;
	}

	public void setStatusLine(final WelcomeStatusLine statusLine) {
		this.statusLine = statusLine;
	}

}
