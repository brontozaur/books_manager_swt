package com.papao.books.ui.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.AutorController;
import com.papao.books.controller.BookController;
import com.papao.books.model.Carte;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.view.SWTeXtension;
import org.bson.types.ObjectId;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.IntegerFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    private String searchQuery;
    private List<ObjectId> idAutori;
    private String colectie;
    private boolean exactSearch;

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
        itemFirstPage.setToolTipText("Prima pagină");
        itemFirstPage.setImage(AppImages.getImage16(AppImages.IMG_HOME));
        itemFirstPage.setHotImage(AppImages.getImage16Focus(AppImages.IMG_HOME));
        itemFirstPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage = 1;
                search(exactSearch);
            }
        });

        itemPrevious = new ToolItem(barFirstpage, SWT.NONE);
        itemPrevious.setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT_OPAL));
        itemPrevious.setToolTipText("Pagina anterioară");
        itemPrevious.setText("pagină");
        itemPrevious.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (currentPage > 0) {
                    currentPage--;
                }
                search(exactSearch);
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
                        SWTeXtension.displayMessageW("Pagină invalida!");
                        return;
                    }
                    currentPage = Integer.valueOf(textGoToPage.getValue().toString());
                    search(exactSearch);
                }
            }
        });

        ToolBar barLastPage = new ToolBar(this, SWT.FLAT | SWT.RIGHT);

        itemNext = new ToolItem(barLastPage, SWT.NONE);
        itemNext.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT_OPAL));
        itemPrevious.setToolTipText("Pagina următoare");
        itemNext.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage++;
                search(exactSearch);
            }
        });

        itemLastPage = new ToolItem(barLastPage, SWT.NONE);
        itemLastPage.setToolTipText("Ultima pagină");
        itemLastPage.setImage(AppImages.getImage16(AppImages.IMG_SHOW));
        itemLastPage.setHotImage(AppImages.getImage16Focus(AppImages.IMG_SHOW));
        itemLastPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                currentPage = totalPages;
                search(exactSearch);
            }
        });

        new Label(this, SWT.NONE).setText("Paginație");

        comboItemsPerPage = new Combo(this, SWT.BORDER | SWT.READ_ONLY);
        comboItemsPerPage.setItems("2", "5", "10", "25", "50", "100", "250", "500");
        comboItemsPerPage.select(5);
        comboItemsPerPage.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                pageSize = Integer.parseInt(comboItemsPerPage.getText());
                currentPage = 1;
                totalPages = 0;
                totalCount = 0;
                search(exactSearch);
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

    public Pageable getPageable(boolean reset) {
        if (reset) {
            setSearchQuery(null);
        }
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

    private void search(boolean exactSearch) {
        this.exactSearch = exactSearch;
        if (searchQuery != null) {
            java.util.List<ObjectId> autori = AutorController.getByNumeCompletLikeIgnoreCaseOrTitluLikeIgnoreCase(searchQuery);
            ApplicationService.getBookController().getByIdIsOrTitluLikeIgnoreCaseOrColectieLikeIgnoreCaseOrSubtitluLikeIgnoreCaseOrIdAutoriContainsOrSerie_NumeLikeIgnoreCaseOrVolumLikeIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(searchQuery, autori, getPageable(false));
        } else if (this.idAutori != null) {
            ApplicationService.getBookController().getByIdAutoriInOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(idAutori, getPageable(false));
        } else if (this.colectie != null) {
            if (exactSearch) {
                ApplicationService.getBookController().getByColectieIsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(colectie, getPageable(false));
            } else {
                ApplicationService.getBookController().getByColectieContainsIgnoreCaseOrderBySerie_NumeAscSerie_VolumAscTitluAscVolumAsc(colectie, getPageable(false));
            }
        } else {
            ApplicationService.getBookController().requestSearch(getPageable(false));
        }
        itemNext.setEnabled(currentPage < totalPages && totalCount > 0);
        itemPrevious.setEnabled(currentPage - 1 > 0 && totalCount > 0);

        updateUI();
    }

    public void setIdAutori(java.util.List<ObjectId> idAutori) {
        this.searchQuery = null;
        this.colectie = null;
        this.idAutori = idAutori;
        totalCount = 0;
        totalPages = 0;
        currentPage = 1;

        updateUI();
        if (this.idAutori != null) {
            search(false);
        }
    }

    public void setColectie(String colectie) {
        this.searchQuery = null;
        this.idAutori = null;
        this.colectie = colectie;
        totalCount = 0;
        totalPages = 0;
        currentPage = 1;

        updateUI();
        if (this.colectie != null) {
            search(true);
        }
    }

    public void setSearchQuery(String searchQuery) {
        this.idAutori = null;
        this.searchQuery = searchQuery;
        totalCount = 0;
        totalPages = 0;
        currentPage = 1;

        updateUI();
        if (this.searchQuery != null) {
            search(exactSearch);
        }
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
