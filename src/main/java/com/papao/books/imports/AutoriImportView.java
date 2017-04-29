package com.papao.books.imports;

import com.papao.books.controller.AbstractController;
import com.papao.books.controller.ApplicationReportController;
import com.papao.books.controller.AutorController;
import com.papao.books.model.Autor;
import com.papao.books.view.AppImages;
import com.papao.books.view.preluari.AbstractPreluareDateM2View;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import java.util.ArrayList;
import java.util.List;

public class AutoriImportView extends AbstractPreluareDateM2View {

    private static final Logger logger = Logger.getLogger(AutoriImportView.class);
    private AutorController autorController;

    public AutoriImportView(Shell parent, ApplicationReportController reportController, AbstractController controller) {
        super(parent, new String[]{"Nume"}, new String[]{"Numele complet al autorului"}, reportController);
        this.autorController = (AutorController) controller;
    }

    @Override
    protected void customizeView() {
        setShellText("Import autori");
        setViewOptions(AbstractView.ADD_CANCEL);
        setBigViewImage(AppImages.getImage24(AppImages.IMG_IMPORT));
    }

    @Override
    protected boolean validate() {
        try {
            if (tableDocumente.getItemCount() == 0) {
                SWTeXtension.displayMessageW("Selectati fisierul de preluare!");
                return false;
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    @Override
    public void save2Db() {
        final TableItem[] items = tableDocumente.getItems();
        List<Integer> succesfullyImportedIndices = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            String autorName = items[i].getText(0);
            Autor autor = autorController.getByNumeComplet(autorName);
            if (autor == null) {
                autor = new Autor();
                autor.setNumeComplet(autorName);
                autorController.save(autor);
                succesfullyImportedIndices.add(i);
            } else {
                items[i].setText(tableDocumente.getColumnCount() - 1, "Exista deja");
                items[i].setBackground(ColorUtil.COLOR_ROSU_SEMI_ROSU);
            }
        }
        Integer[] array = succesfullyImportedIndices.toArray(new Integer[succesfullyImportedIndices.size()]);
        tableDocumente.remove(ArrayUtils.toPrimitive(array));
        if (succesfullyImportedIndices.size() == items.length) {
            SWTeXtension.displayMessageI("Toti autorii au fost importati cu succes!");
        } else {
            SWTeXtension.displayMessageI("S-au importat cu succes doar " + succesfullyImportedIndices.size() + " din " + items.length + ".");
        }
    }
}
