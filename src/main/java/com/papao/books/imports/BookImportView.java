package com.papao.books.imports;

import com.papao.books.ApplicationService;
import com.papao.books.controller.AutorController;
import com.papao.books.model.Autor;
import com.papao.books.model.Carte;
import com.papao.books.view.AppImages;
import com.papao.books.view.preluari.AbstractPreluareDateM2View;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.List;

public class BookImportView extends AbstractPreluareDateM2View {

    private static final Logger logger = Logger.getLogger(BookImportView.class);
    private final static int IDX_AUTOR = 0;
    private final static int IDX_BOOK_TITLE = 1;

    public BookImportView(Shell parent) {
        super(parent, new String[]{"Autor", "Titlu"}, new String[]{"Numele complet al autorului", "Titlul cartii"});
    }

    @Override
    protected void customizeView() {
        setShellText("Import carti");
        setViewOptions(AbstractView.ADD_CANCEL);
        setBigViewImage(AppImages.getImage24(AppImages.IMG_IMPORT));
    }

    @Override
    protected boolean validate() {
        if (tableDocumente.getItemCount() == 0) {
            SWTeXtension.displayMessageW("Selectati fisierul de preluare!");
            return false;
        }
        return true;
    }

    private List<ObjectId> getAuthorIds(String name) {
        String[] autorNames = name.split(",");
        List<ObjectId> idAutori = new ArrayList<>();
        for (String autorName : autorNames) {
            autorName = autorName.trim();
            if (StringUtils.isEmpty(autorName)) {
                continue;
            }
            Autor autor = AutorController.getByNumeComplet(autorName);
            if (autor == null) {
                autor = new Autor();
                autor.setNumeComplet(autorName);
                autor = AutorController.save(autor);
            }
            idAutori.add(autor.getId());
        }
        return idAutori;
    }

    @Override
    public void save2Db() {
        final TableItem[] items = tableDocumente.getItems();
        List<Integer> succesfullyImportedIndices = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            String authorsName = items[i].getText(IDX_AUTOR);
            String bookTitle = items[i].getText(IDX_BOOK_TITLE).trim();
            List<ObjectId> authorIds = getAuthorIds(authorsName);
            Carte carte = ApplicationService.getBookController().getByTitluAndIdAutori(bookTitle, authorIds);
            if (carte == null) {
                carte = new Carte();
                carte.setTitlu(bookTitle);
                carte.setIdAutori(authorIds);
                ApplicationService.getBookController().save(carte);
                succesfullyImportedIndices.add(i);
            } else {
                items[i].setText(tableDocumente.getColumnCount() - 1, "Exista deja");
                items[i].setBackground(ColorUtil.COLOR_ROSU_SEMI_ROSU);
            }
        }
        Integer[] array = succesfullyImportedIndices.toArray(new Integer[succesfullyImportedIndices.size()]);
        tableDocumente.remove(ArrayUtils.toPrimitive(array));
        if (succesfullyImportedIndices.size() == items.length) {
            SWTeXtension.displayMessageI("Tate cartile au fost importate cu succes!");
        } else {
            SWTeXtension.displayMessageI("S-au importat cu succes doar " + succesfullyImportedIndices.size() + " carti din " + items.length + ".");
        }
    }

    protected void pasteFromClipboard() {
        clearTable(true);
        Clipboard clipboard = new Clipboard(Display.getDefault());
        String plainText = (String) clipboard.getContents(TextTransfer.getInstance());
        clipboard.dispose();
        String[] rows = plainText.split("\n");

        for (String row : rows) {
            if (!row.contains(getDelimitator())) {
                logger.error("line: [" + row + "] was skipped");
                continue;
            }
            String[] values = new String[2];
            values[0] = row.substring(0, row.indexOf(getDelimitator()));
            values[1] = row.substring(values[0].length() + getDelimitator().length());
            new TableItem(tableDocumente, SWT.NONE).setText(values);
        }
        itemValidateFile.setEnabled(tableDocumente.getItemCount() > 0);
        itemPreluare.setEnabled(false);
    }
}
