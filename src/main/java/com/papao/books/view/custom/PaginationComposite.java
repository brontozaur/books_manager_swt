package com.papao.books.view.custom;

import com.papao.books.controller.CartePaginationController;
import com.papao.books.model.Carte;
import com.papao.books.view.AppImages;
import com.papao.books.view.searcheable.BookSearchType;
import com.papao.books.view.util.NumberUtil;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Observable;
import java.util.Observer;

public class PaginationComposite extends Composite implements Observer {

    private FormattedText textGoToPage;
    private Combo comboItemsPerPage;
    private Label labelShowingXItemsOfTotal;
    private ToolItem itemPrevious;
    private ToolItem itemNext;
    private long totalCount = 0;
    private long totalPages = 0;
    private int currentPage = 0;
    private int pageSize = 0;
    private CartePaginationController paginationController;
    private BookSearchType searchType;

    public PaginationComposite(Composite parent, CartePaginationController paginationController, BookSearchType searchType) {
        super(parent, SWT.NONE);
        this.paginationController = paginationController;
        paginationController.addObserver(this);
        this.searchType = searchType;

        GridLayoutFactory.fillDefaults().numColumns(6).equalWidth(false).applyTo(this);

        Label tmp = new Label(this, SWT.NONE);
        tmp.setText("Afisare pagina");
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(tmp);

        textGoToPage = new FormattedText(this, SWT.BORDER);
        textGoToPage.setFormatter(NumberUtil.getFormatter(0, true));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).minSize(50, SWT.DEFAULT).hint(50, SWT.DEFAULT).applyTo(textGoToPage.getControl());
        textGoToPage.getControl().addListener(SWT.KeyDown, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.keyCode == SWT.CR) {
                    if (!validatePageNumber(event)) {
                        SWTeXtension.displayMessageW("Pagina invalida!");
                        return;
                    }
                    currentPage = Integer.valueOf(textGoToPage.getValue().toString());
                    search();
                }
            }
        });

        tmp = new Label(this, SWT.NONE);
        tmp.setText("Documente pe pagina");
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(tmp);

        comboItemsPerPage = new Combo(this, SWT.BORDER | SWT.READ_ONLY);
        comboItemsPerPage.setItems(new String[]{"10", "25", "50", "100", "250", "500"});
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(comboItemsPerPage);
        comboItemsPerPage.select(4);
        comboItemsPerPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                pageSize = Integer.parseInt(comboItemsPerPage.getText());
                search();
            }
        });

        labelShowingXItemsOfTotal = new Label(this, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(150, SWT.DEFAULT).applyTo(labelShowingXItemsOfTotal);

        ToolBar bar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(bar);

        itemPrevious = new ToolItem(bar, SWT.NONE);
        itemPrevious.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_BACK));
        itemPrevious.setToolTipText("Pagina anterioara");
        itemPrevious.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (currentPage > 0) {
                    currentPage--;
                }
                search();
            }
        });

        itemNext = new ToolItem(bar, SWT.NONE);
        itemNext.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIMPLE_NEXT));
        itemPrevious.setToolTipText("Pagina urmatoare");
        itemNext.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage++;
                search();
            }
        });

        search();
    }

    private boolean validatePageNumber(Event event) {
        if (event.character != SWT.CR && !Character.isDigit(event.character)) {
            return false;
        }
        int proposedNewPage;
        if (event.character == SWT.CR) {
            proposedNewPage = Integer.valueOf(textGoToPage.getControl().getText());
        } else {
            proposedNewPage = Integer.valueOf(String.valueOf(event.character));
        }
        if (totalCount == 0 || totalPages - 1 < proposedNewPage) {
            return false;
        }
        return true;
    }

    private String getLabelText() {
        if (totalCount == 0) {
            return "No documents found.";
        }
        long min = currentPage * pageSize + 1;
        long max = pageSize * (currentPage + 1);
        if (max > totalCount) {
            max = totalCount;
        }
        return min + "-" + max + " of " + totalCount;
    }

    public Pageable getPageable() {
        pageSize = Integer.parseInt(comboItemsPerPage.getText());
        return new PageRequest(currentPage, pageSize);
    }

    @Override
    public void update(Observable o, Object arg) {
        CartePaginationController controller = (CartePaginationController) o;
        Page<Carte> page = controller.getSearchResult();
        totalCount = page.getTotalElements();
        totalPages = page.getTotalPages();
        labelShowingXItemsOfTotal.setText(getLabelText());
        textGoToPage.setValue(page.getNumber());
    }

    public BookSearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(BookSearchType searchType) {
        this.searchType = searchType;
    }

    private void search() {
        paginationController.requestSearch(searchType, null, getPageable());
        itemNext.setEnabled(currentPage < totalPages - 1 && totalCount > 0);
        itemPrevious.setEnabled(currentPage > 0 && totalCount > 0);
    }
}
