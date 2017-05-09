package com.papao.books.view.preluari;

import com.papao.books.view.AppImages;
import com.papao.books.view.custom.ProgressBarComposite;
import com.papao.books.view.interfaces.IReset;
import com.papao.books.view.util.WidgetTableUtil;
import com.papao.books.view.view.AbstractCViewAdapter;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public abstract class AbstractPreluareDateM1View extends AbstractCViewAdapter implements IReset {

    private static final Logger logger = Logger.getLogger(AbstractPreluareDateM1View.class);

    private ToolItem itemOpenFile;
    private ToolItem itemValidateFile;
    private ToolItem itemPreluare;
    private ToolBar barOps;
    private ProgressBarComposite cpBar;
    public boolean ready4Import = false;
    private Table tableDocumente;

    public AbstractPreluareDateM1View(final Shell parent) {
        super(parent, AbstractView.MODE_NONE);
        addComponents();
    }

    private void addComponents() {
        Composite compSuport;

        compSuport = new Composite(getContainer(), SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compSuport);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(compSuport);

        setBarOps(new ToolBar(compSuport, SWT.RIGHT | SWT.FLAT));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.barOps);

        setItemOpenFile(new ToolItem(getBarOps(), SWT.PUSH));
        getItemOpenFile().setImage(AppImages.getImage16(AppImages.IMG_IMPORT));
        getItemOpenFile().setHotImage(AppImages.getImage16Focus(AppImages.IMG_IMPORT));
        getItemOpenFile().setToolTipText("Import fisier (prima linie este rezervata pentru denumirea coloanelor)");
        getItemOpenFile().setText("Import");
        getItemOpenFile().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                loadDataFile();
            }
        });

        setItemValidateFile(new ToolItem(getBarOps(), SWT.PUSH));
        itemValidateFile.setImage(AppImages.getImage16(AppImages.IMG_OK));
        itemValidateFile.setHotImage(AppImages.getImage16Focus(AppImages.IMG_OK));
        itemValidateFile.setToolTipText("Validare fisier");
        itemValidateFile.setText("Validare");
        itemValidateFile.setEnabled(false);
        itemValidateFile.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                try {
                    getTableDocumente().setFocus();
                    AbstractPreluareDateM1View.this.ready4Import = validate();
                    getItemPreluare().setEnabled(AbstractPreluareDateM1View.this.ready4Import);
                } catch (Exception exc) {
                    logger.error(exc, exc);
                    SWTeXtension.displayMessageE(exc.getMessage(), exc);
                }
            }
        });

        setItemPreluare(new ToolItem(getBarOps(), SWT.PUSH));
        getItemPreluare().setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
        getItemPreluare().setHotImage(AppImages.getImage16Focus(AppImages.IMG_EXPORT));
        getItemPreluare().setToolTipText("Start preluare");
        getItemPreluare().setText("Preluare");
        getItemPreluare().setEnabled(false);
        getItemPreluare().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                preluareDate();
            }
        });

        setCpBar(new ProgressBarComposite(compSuport, Integer.MAX_VALUE, SWT.SMOOTH));
        getCpBar().setVisible(false);

        setTableDocumente(new Table(getContainer(), SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL));
        getTableDocumente().setLinesVisible(true);
        getTableDocumente().setHeaderVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).hint(640, 480).applyTo(getTableDocumente());
        SWTeXtension.addColoredFocusListener(getTableDocumente(), null);
        WidgetTableUtil.addCustomGradientSelectionListenerToTable(getTableDocumente(), null, null);
        getTableDocumente().setMenu(createTableMenu());
        getTableDocumente().addListener(SWT.KeyDown, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                if (e.widget == getTableDocumente()) {
                    if (SWTeXtension.getDeleteTrigger(e)) {
                        del();
                    }
                }
            }
        });
        getTableDocumente().addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                mod();
            }
        });
    }

    private Menu createTableMenu() {
        if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
            return null;
        }
        final Menu menu = new Menu(getTableDocumente());
        MenuItem menuItem;
        try {
            menu.addListener(SWT.Show, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    int idx = 0;
                    final int selIdx = getTableDocumente().getSelectionIndex();
                    try {
                        menu.getItem(idx++).setEnabled(getTableDocumente().getColumnCount() > 0); // add
                        menu.getItem(idx++).setEnabled(selIdx != -1); // mod
                        menu.getItem(idx++).setEnabled(selIdx != -1); // del
                    } catch (Exception exc) {
                        logger.error(exc, exc);
                        SWTeXtension.displayMessageEGeneric(exc);
                    }
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Adaugare");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    add();
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Modificare");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    mod();
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Stergere	Del");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    del();
                }
            });
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
        return menu;
    }

    private void preluareDate() {
        try {
            if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
                return;
            }
            if (getTableDocumente().getItemCount() == 0) {
                SWTeXtension.displayMessageW("Nu avem ce prelua...");
                return;
            }
            if (!save2Db()) {
                return;
            }
            SWTeXtension.displayMessageI("Preluarea s-a efectuat cu succes!");
            clearTable(false);
            getItemPreluare().setEnabled(false);
            itemValidateFile.setEnabled(false);
            this.ready4Import = false;
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    private void clearTable(final boolean ask) {
        try {
            if ((getTableDocumente() == null) || getTableDocumente().isDisposed()) {
                getItemPreluare().setEnabled(false);
                return;
            }
            if ((getTableDocumente().getItemCount() > 0) && ask) {
                if (SWTeXtension.displayMessageQ("Sunteti sigur ca doriti sa stergeti liniile importate?", "Stergere linii importate") == SWT.NO) {
                    return;
                }
            }
            getItemPreluare().setEnabled(false);
            itemValidateFile.setEnabled(false);
            getTableDocumente().removeAll();
            if (getCpBar().isVisible()) {
                getCpBar().setVisible(false);
            }
            this.ready4Import = false;
            TableColumn[] cols = getTableDocumente().getColumns();
            for (int i = 0; i < cols.length; i++) {
                getTableDocumente().getColumn(0).dispose();
            }
            getTableDocumente().layout();
        } catch (Exception exc) {
            logger.error(exc, exc);
            SWTeXtension.displayMessageEGeneric(exc);
        }
    }

    public abstract void loadDataFile();

    public abstract boolean save2Db();

    public abstract void add();

    public abstract void mod();

    public abstract void del();

    @Override
    public final void reset() {
        clearTable(true);
    }

    public final ToolItem getItemOpenFile() {
        return this.itemOpenFile;
    }

    public final void setItemOpenFile(final ToolItem itemOpenFile) {
        this.itemOpenFile = itemOpenFile;
    }

    public final ToolItem getItemValidateFile() {
        return this.itemValidateFile;
    }

    public final void setItemValidateFile(final ToolItem itemValidateFile) {
        this.itemValidateFile = itemValidateFile;
    }

    public final ToolItem getItemPreluare() {
        return this.itemPreluare;
    }

    public final void setItemPreluare(final ToolItem itemPreluare) {
        this.itemPreluare = itemPreluare;
    }

    public final ToolBar getBarOps() {
        return this.barOps;
    }

    public final void setBarOps(final ToolBar barOps) {
        this.barOps = barOps;
    }

    public final ProgressBarComposite getCpBar() {
        return this.cpBar;
    }

    public final void setCpBar(final ProgressBarComposite cpBar) {
        this.cpBar = cpBar;
    }

    public final Table getTableDocumente() {
        return this.tableDocumente;
    }

    public final void setTableDocumente(final Table tableDocumente) {
        this.tableDocumente = tableDocumente;
    }

}
