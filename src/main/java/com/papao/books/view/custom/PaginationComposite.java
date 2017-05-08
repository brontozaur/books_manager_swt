package com.papao.books.view.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.BookController;
import com.papao.books.model.Carte;
import com.papao.books.view.AppImages;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.IntegerFormatter;
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
    private ToolItem itemFirstPage;
    private ToolItem itemNext;
    private ToolItem itemLastPage;

    private long totalCount = 0;
    private long totalPages = 0;
    private long currentPage = 1;
    private int pageSize = 0;

    public PaginationComposite(Composite parent) {
        super(parent, SWT.NONE);
        ApplicationService.getBookController().addObserver(this);

        GridLayoutFactory.fillDefaults().numColumns(6).margins(0, 0).spacing(5, 0).extendedMargins(0, 0, 5, 0).equalWidth(false).applyTo(this);

        ToolBar barFirstpage = new ToolBar(this, SWT.FLAT | SWT.RIGHT);

        itemFirstPage = new ToolItem(barFirstpage, SWT.NONE);
        itemFirstPage.setToolTipText("Prima pagina");
        itemFirstPage.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        itemFirstPage.setHotImage(AppImages.getImage16Focus(AppImages.IMG_HOME));
        itemFirstPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage = 1;
                search();
            }
        });

        itemPrevious = new ToolItem(barFirstpage, SWT.NONE);
        itemPrevious.setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT_OPAL));
        itemPrevious.setToolTipText("Pagina anterioara");
        itemPrevious.setText("pagina");
        itemPrevious.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (currentPage > 0) {
                    currentPage--;
                }
                search();
            }
        });

        textGoToPage = new FormattedText(this, SWT.BORDER);
        textGoToPage.setFormatter(new IntegerFormatter());
        GridDataFactory.fillDefaults().minSize(30, SWT.DEFAULT).hint(30, SWT.DEFAULT).applyTo(textGoToPage.getControl());
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

        ToolBar barLastPage = new ToolBar(this, SWT.FLAT | SWT.RIGHT);

        itemNext = new ToolItem(barLastPage, SWT.NONE);
        itemNext.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
        itemPrevious.setToolTipText("Pagina urmatoare");
        itemNext.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage++;
                search();
            }
        });

        itemLastPage = new ToolItem(barLastPage, SWT.NONE);
        itemLastPage.setToolTipText("Ultima pagina");
        itemLastPage.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        itemLastPage.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SHOW));
        itemLastPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage = totalPages;
                search();
            }
        });

        new Label(this, SWT.NONE).setText("Paginatie");

        comboItemsPerPage = new Combo(this, SWT.BORDER | SWT.READ_ONLY);
        comboItemsPerPage.setItems(new String[]{"2", "5", "10", "25", "50", "100", "250", "500"});
        comboItemsPerPage.select(5);
        comboItemsPerPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                pageSize = Integer.parseInt(comboItemsPerPage.getText());
                currentPage = 1;
                totalPages = 0;
                totalCount = 0;
                search();
            }
        });

        labelShowingXItemsOfTotal = new Label(this, SWT.NONE);
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
        if (totalCount == 0 || totalPages < proposedNewPage) {
            return false;
        }
        return true;
    }

    private String getLabelText() {
        if (totalCount == 0) {
            return "Nu exista documente.";
        }
        long min = (currentPage - 1) * pageSize;
        long max = pageSize * currentPage;
        if (max > totalCount) {
            max = totalCount;
        }
        return min + "-" + max + " din " + totalCount;
    }

    public Pageable getPageable() {
        pageSize = Integer.parseInt(comboItemsPerPage.getText());
        return new PageRequest((int) currentPage - 1, pageSize);
    }

    @Override
    public void update(Observable o, Object arg) {
        BookController controller = (BookController) o;
        Page<Carte> page = controller.getSearchResult();
        totalCount = page.getTotalElements();
        totalPages = page.getTotalPages();
        currentPage = page.getNumber() + 1;

        updateUI();
    }

    private void search() {
        //TODO search from db directly
        ApplicationService.getBookController().requestSearch(getPageable());
        itemNext.setEnabled(currentPage < totalPages && totalCount > 0);
        itemPrevious.setEnabled(currentPage - 1 > 0 && totalCount > 0);

        updateUI();
    }

    private void updateUI() {
        if (this.isDisposed()) {
            return;
        }
        itemNext.setEnabled(currentPage - 1 < totalPages - 1 && totalCount > 0);
        itemLastPage.setEnabled(totalPages > 1 && itemNext.getEnabled());

        itemPrevious.setEnabled(currentPage - 1 > 0 && totalCount > 0);
        itemFirstPage.setEnabled(totalPages > 1 && itemPrevious.getEnabled());

        labelShowingXItemsOfTotal.setText(getLabelText());
//        labelShowingXItemsOfTotal.setSize(labelShowingXItemsOfTotal.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        textGoToPage.setValue(currentPage);
        itemNext.setText("din " + totalPages);
        layout();
    }
}
