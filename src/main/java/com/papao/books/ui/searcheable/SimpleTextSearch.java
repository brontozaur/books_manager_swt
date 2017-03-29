package com.papao.books.ui.searcheable;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.view.SWTeXtension;
import com.papao.books.util.ColorUtil;
import com.papao.books.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;

public class SimpleTextSearch extends AbstractSearchType {

    private ToolItem itemOptionsOK;
    private Text textCriteriuOK;

    private boolean exactMatch = false;
    private boolean caseSensitive = false;
    private boolean contains = true;
    private boolean startsWith = false;
    private boolean endsWith = false;
    private boolean notStartsWith = false;
    private boolean notEndsWith = false;
    private boolean notContains = false;

    public SimpleTextSearch(final BorgSearchSystem searchSystem, final String colName) {
        super(searchSystem, colName);
    }

    @Override
    public final void createContents() {
        Composite comp;

        comp = new Composite(this, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(5, 0, 0, 0).spacing(0, 0).applyTo(comp);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(comp);

        this.textCriteriuOK = new Text(comp, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(85, SWT.DEFAULT).minSize(85, SWT.DEFAULT).applyTo(this.textCriteriuOK);
        SWTeXtension.addColoredFocusListener(this.textCriteriuOK, ColorUtil.COLOR_FOCUS_YELLOW);
        this.textCriteriuOK.setToolTipText("Introduceti una sau mai multe valori (separate prin virgula)");

        this.itemOptionsOK = new ToolItem(new ToolBar(this, SWT.FLAT), SWT.NONE);
        this.itemOptionsOK.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_MENU));
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(this.itemOptionsOK.getParent());
        this.itemOptionsOK.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final Rectangle itemBounds = SimpleTextSearch.this.itemOptionsOK.getBounds();
                final Point point = SimpleTextSearch.this.itemOptionsOK.getParent().toDisplay(new Point(itemBounds.x, itemBounds.y));
                SimpleTextSearch.this.itemOptionsOK.getParent().getMenu().setLocation(point.x, point.y + itemBounds.height);
                SimpleTextSearch.this.itemOptionsOK.getParent().getMenu().setVisible(true);
            }
        });
        this.itemOptionsOK.setToolTipText("Optiuni valori incluse");
        this.itemOptionsOK.getParent().setMenu(createOptionsOKMenu());

        this.itemOptionsOK.getParent().setBackgroundMode(SWT.INHERIT_DEFAULT);

        this.textCriteriuOK.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                getSearchSystem().getSearchButton().notifyListeners(SWT.MouseUp, new Event());
            }
        });
        this.textCriteriuOK.setFocus();
    }

    private Menu createOptionsOKMenu() {
        if (this.itemOptionsOK == null) {
            return null;
        }
        final Menu menu = new Menu(this.itemOptionsOK.getParent());
        this.itemOptionsOK.getParent().setMenu(menu);

        menu.addListener(SWT.Show, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                handleRelationsInOKMenu(menu, null, false);
            }
        });

        MenuItem item = new MenuItem(menu, SWT.CHECK);
        item.setText("cautare exacta");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                setExactMatch(it.getSelection());
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        item = new MenuItem(menu, SWT.CHECK);
        item.setText("case senzitiv");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                setCaseSensitive(it.getSelection());
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        item = new MenuItem(menu, SWT.CHECK);
        item.setText("contine");
        item.setSelection(true);
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                setContains(it.getSelection());
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        item = new MenuItem(menu, SWT.CHECK);
        item.setText("incepe cu");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                setStartsWith(it.getSelection());
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        item = new MenuItem(menu, SWT.CHECK);
        item.setText("se termina cu");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                setEndsWith(it.getSelection());
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        item = new MenuItem(menu, SWT.SEPARATOR);

        item = new MenuItem(menu, SWT.CHECK);
        item.setText("Reset");
        item.addListener(SWT.Selection, new Listener() {
            @Override
            public final void handleEvent(final Event e) {
                MenuItem it = ((MenuItem) e.widget);
                handleRelationsInOKMenu(menu, it, true);
            }
        });

        return menu;
    }

    public final void handleRelationsInOKMenu(final Menu menu, final MenuItem selected, final boolean reafisare) {
        int IDX_EXACT_MATCH = 0;
        @SuppressWarnings("unused")
        int IDX_CASE_SENSITIVE = 1;
        int IDX_CONTAINS = 2;
        int IDX_STARTS_WITH = 3;
        int IDX_ENDS_WITH = 4;
        int IDX_RESET = 6;

        if ((selected == menu.getItem(IDX_STARTS_WITH))) {
            if (selected.getSelection()) {
                menu.getItem(IDX_ENDS_WITH).setSelection(false);
                menu.getItem(IDX_CONTAINS).setSelection(false);
                setEndsWith(false);
                setContains(false);
            }
            menu.getItem(IDX_STARTS_WITH).setSelection(selected.getSelection());
            setStartsWith(selected.getSelection());
        } else if ((selected == menu.getItem(IDX_CONTAINS))) {
            if (selected.getSelection()) {
                menu.getItem(IDX_ENDS_WITH).setSelection(false);
                menu.getItem(IDX_STARTS_WITH).setSelection(false);
                setEndsWith(false);
                setStartsWith(false);
            }
            menu.getItem(IDX_CONTAINS).setSelection(selected.getSelection());
            setContains(selected.getSelection());
        } else if ((selected == menu.getItem(IDX_ENDS_WITH))) {
            if (selected.getSelection()) {
                menu.getItem(IDX_STARTS_WITH).setSelection(false);
                menu.getItem(IDX_CONTAINS).setSelection(false);
                setStartsWith(false);
                setContains(false);
            }
            menu.getItem(IDX_ENDS_WITH).setSelection(selected.getSelection());
            setEndsWith(selected.getSelection());
        } else if (selected == menu.getItem(IDX_EXACT_MATCH)) {
            menu.getItem(IDX_CONTAINS).setEnabled(!selected.getSelection());
            menu.getItem(IDX_STARTS_WITH).setEnabled(!selected.getSelection());
            menu.getItem(IDX_ENDS_WITH).setEnabled(!selected.getSelection());
        } else if (selected == menu.getItem(IDX_RESET)) {
            for (MenuItem it : menu.getItems()) {
                it.setSelection(false);
                it.setEnabled(true);
            }
            setExactMatch(false);
            setStartsWith(false);
            setEndsWith(false);
            setCaseSensitive(false);
            menu.getItem(IDX_CONTAINS).setSelection(true);
            setContains(true);
        }

        if (reafisare) {
            final Rectangle itemBounds = this.itemOptionsOK.getBounds();
            final Point point = this.itemOptionsOK.getParent().toDisplay(new Point(itemBounds.x, itemBounds.y));
            menu.setLocation(point.x, point.y + itemBounds.height);
            menu.setVisible(true);
        }
    }

    public String[] getValue() {
        if ((this.textCriteriuOK == null) || this.textCriteriuOK.isDisposed()) {
            return new String[0];
        }
		return this.textCriteriuOK.getText().replaceAll(" ", "").split(",");
    }

    @Override
    public boolean isModified() {
		boolean result = StringUtils.isNotEmpty(this.textCriteriuOK.getText());
        getLabelName().setForeground(result ? AbstractSearchType.FILTRU_ACTIV : AbstractSearchType.FILTRU_INACTIV);
        return result;
    }

    public final boolean compareStrings(final String full) {
		if ((getValue() == null) || ((getValue().length == 1) && StringUtils.isEmpty(getValue()[0]))) {
            return true;
        }
        final int lenght = getValue().length;
        for (int i = 0; i < lenght; i++) {
            if (compareStrings(getValue()[i], full)) {
                return true;
            }
        }
        return false;
    }

    public final boolean compareStrings(final String sirCautat, final String full) {
        return StringUtil.compareStrings(sirCautat,
                full,
                isCaseSensitive(),
                isExactMatch(),
                isContains(),
                isStartsWith(),
                isEndsWith());
    }

    @Override
    public boolean compareValues(Object valueToBeCompared) {
        if (valueToBeCompared == null) {
            return true;
        }
        return compareStrings(valueToBeCompared.toString());
    }

    public final boolean isExactMatch() {
        return this.exactMatch;
    }

    public final void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    public final boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public final void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public final boolean isContains() {
        return this.contains;
    }

    public final void setContains(boolean contains) {
        this.contains = contains;
    }

    public final boolean isStartsWith() {
        return this.startsWith;
    }

    public final void setStartsWith(boolean startsWith) {
        this.startsWith = startsWith;
    }

    public final boolean isEndsWith() {
        return this.endsWith;
    }

    public final void setEndsWith(boolean endsWith) {
        this.endsWith = endsWith;
    }

    public final boolean isNotStartsWith() {
        return this.notStartsWith;
    }

    public final void setNotStartsWith(boolean notStartsWith) {
        this.notStartsWith = notStartsWith;
    }

    public final boolean isNotEndsWith() {
        return this.notEndsWith;
    }

    public final void setNotEndsWith(boolean notEndsWith) {
        this.notEndsWith = notEndsWith;
    }

    public final boolean isNotContains() {
        return this.notContains;
    }

    public final void setNotContains(boolean notContains) {
        this.notContains = notContains;
    }
}
