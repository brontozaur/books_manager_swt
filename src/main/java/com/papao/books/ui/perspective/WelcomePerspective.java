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
        booksTabItem.setImage(AppImages.getImage32(AppImages.IMG_DETAILS_NEW));
        booksTabItem.setControl(createBooksGrid());

        this.mainTabFolder.setSelection(0);

        createTopRightComponents(mainTabFolder);

        Composite lowerCompBarDocking = new Composite(this, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).grab(true, false).hint(SWT.DEFAULT, 20).applyTo(lowerCompBarDocking);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).extendedMargins(0,
				0,
				0,
				0).spacing(0, 0).applyTo(lowerCompBarDocking);
		lowerCompBarDocking.setBackground(ColorUtil.COLOR_WHITE);
		lowerCompBarDocking.setBackgroundMode(SWT.INHERIT_DEFAULT);

		this.barDocking = new ToolBar(lowerCompBarDocking, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(this.barDocking);
		GridLayoutFactory.fillDefaults().margins(0,0).applyTo(this.barDocking);
		this.barDocking.setMenu(createBarDockingMenu());

		setStatusLine(new WelcomeStatusLine(this));
		getStatusLine().getLabelNumeModul().setText("Selectati un modul");
	}

	private void createTopRightComponents(Composite parent) {
        ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT | SWT.WRAP);
        bar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_ARROW_RIGHT));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_RIGHT));
        item.setToolTipText("Logout");
        item.setText("Logout");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.logout(true);
            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_CANCEL));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CANCEL));
        item.setToolTipText("Exit");
        item.setText("Exit");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                EncodePlatform.instance.performShellClose(new Event());
            }
        });
		this.mainTabFolder.setTopRight(bar);
    }

	private Composite createBooksGrid(){
	    Composite composite = new Composite(this.mainTabFolder, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(composite);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(0, 0, 0, 0).applyTo(composite);
        Composite group = new Canvas(composite, SWT.NONE);
        group.setBackground(ColorUtil.COLOR_FOCUS_YELLOW);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
        GridLayoutFactory.fillDefaults().numColumns(1).extendedMargins(2, 2, 5, 2).applyTo(group);
        group.addListener(SWT.Paint, new Listener() {

            @Override
            public void handleEvent(final Event e) {
                e.gc.setForeground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
                e.gc.drawRoundRectangle(0,
                        0,
                        getClientArea().width - 8,
                        getClientArea().height -6,
                        8,
                        8);

            }
        });

//        composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        Composite up = new Group(group, SWT.BORDER);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(up);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(up);

        ToolBar bar = new ToolBar(up, SWT.NONE);
//        bar.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

        ToolItem item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_PLUS));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_PLUS));
        item.setToolTipText("Adauga o carte noua");
        item.setText("Adauga");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_UPDATE));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_UPDATE));
        item.setToolTipText("Schimba detaliile cartii selectate");
        item.setText("Modifica");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_STOP));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_STOP));
        item.setToolTipText("Sterge cartea selectata");
        item.setText("Sterge");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });

        item = new ToolItem(bar, SWT.NONE);
        item.setImage(AppImages.getImage24(AppImages.IMG_DETAILS_NEW));
        item.setHotImage(AppImages.getImage24Focus(AppImages.IMG_DETAILS_NEW));
        item.setToolTipText("Vizualizare detalii");
        item.setText("Vezi");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {

            }
        });


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
