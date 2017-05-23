package com.papao.books.export;

import com.inamik.utils.SimpleTableFormatter;
import com.inamik.utils.TableFormatter;
import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.ApplicationService;
import com.papao.books.controller.ApplicationController;
import com.papao.books.controller.ReportController;
import com.papao.books.model.ApplicationReport;
import com.papao.books.model.Carte;
import com.papao.books.model.CarteExport;
import com.papao.books.ui.auth.EncodeLive;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.util.ObjectUtil;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SerializareCompletaView {

    private static final Logger logger = Logger.getLogger(SerializareCompletaView.class);

    private Shell parent;

    public SerializareCompletaView(Shell parent) {
        this.parent = parent;
    }

    private String getExtension(String filePath) {
        if (filePath == null) {
            return ".jpg";
        }
        if (filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."));
        }
        return ".jpg";
    }

    public void export() {
        PrintStream ps = null;
        CWaitDlgClassic dlgClassic = null;
        boolean showBorder;
        boolean showNrCrt;
        boolean showTitle;
        String titleName;
        String fileName;
        boolean serializareImagini;
        String selectedImagesFolder;
        List<String> sortProperties;

        List<FieldColumnValue> selectedFields;
        try {
            SerializareCompletaOptionsView view = new SerializareCompletaOptionsView(parent);
            view.open();
            if (view.getUserAction() == SWT.OK) {
                showBorder = view.showBorder;
                showNrCrt = view.showNrCrt;
                showTitle = view.showTitle;
                titleName = view.titleName;
                fileName = view.fileName;
                serializareImagini = view.serializareImagini;
                selectedImagesFolder = view.selectedImagesFolder;
                selectedFields = view.selectedFields;
                sortProperties = view.sortProperties;
            } else {
                return;
            }

            dlgClassic = new CWaitDlgClassic("Va rugam asteptati generarea fisierului...");
            dlgClassic.open();
            File output;

            if (StringUtils.isEmpty(fileName)) {
                fileName = "Carti_" + System.currentTimeMillis();
            }

            if (fileName.toLowerCase().endsWith(".txt")) {
                output = new File(fileName);
            } else {
                output = new File(fileName + ".txt");
            }

            SimpleTableFormatter tf = new SimpleTableFormatter(showBorder);
            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell(TableFormatter.ALIGN_CENTER, TableFormatter.VALIGN_CENTER);
                tf.addLine("Nr crt.");
            }
            for (int i = 0; i < selectedFields.size(); i++) {
                tf.nextCell(getAlign(selectedFields.get(i).getAlign()), TableFormatter.VALIGN_CENTER);
                tf.addLine(selectedFields.get(i).getFieldName());
            }

            List<Carte> allBooks = ApplicationService.getBookController().getRepository().findAll();
            List<CarteExport> exportBooks = new ArrayList<>();

            for (int i = 0; i < allBooks.size(); i++) {
                Carte carte = allBooks.get(i);
                CarteExport ce = new CarteExport(carte);
                exportBooks.add(ce);
                if (serializareImagini) {
                    if (carte.getCopertaFata().exists()) {
                        GridFSDBFile image = ApplicationController.getDocumentData(carte.getCopertaFata().getId());
                        if (image != null) {
                            String temp = selectedImagesFolder + File.separator +
                                    ce.getAutori() + " - " + ce.getTitlu().replaceAll("[^a-zA-Z0-9.-]", "_") + getExtension((String) image.getMetaData().get("localFilePath"));
                            File file = new File(temp);
                            image.writeTo(file);
                        } else {
                            logger.error("Image not found for book with id " + ce.getId());
                        }
                    }
                }
            }

            if (sortProperties != null && !sortProperties.isEmpty()) {
                exportBooks.sort((o1, o2) -> {
                    try {
                        StringBuilder complexPropertyA = new StringBuilder();
                        StringBuilder complexPropertyB = new StringBuilder();
                        for (String sortProperty : sortProperties) {
                            Method method = ObjectUtil.getMethod(CarteExport.class, "get" + StringUtils.capitalize(sortProperty));
                            String a = (String) method.invoke(o1, (Object[]) null);
                            String b = (String) method.invoke(o2, (Object[]) null);
                            complexPropertyA.append(a != null ? a : " ");
                            complexPropertyB.append(b != null ? b : " ");
                        }
                        return complexPropertyA.toString().compareToIgnoreCase(complexPropertyB.toString());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        SWTeXtension.displayMessageE(e.getMessage(), e);
                    }
                    return 0;
                });
            } else {
                exportBooks.sort(Comparator.comparing(CarteExport::getAutori));
            }

            dlgClassic.setMax(exportBooks.size());
            dlgClassic.open();

            tf.nextRow();
            if (showNrCrt) {
                tf.nextCell();
                tf.addLine("");
            }
            for (int i = 0; i < selectedFields.size(); i++) {
                tf.nextCell();
                tf.addLine("");
            }

            for (int i = 0; i < exportBooks.size(); i++) {
                if (i % 5 == 0) {
                    dlgClassic.advance(i);
                    Display.getDefault().readAndDispatch();
                }
                tf.nextRow();
                if (showNrCrt) {
                    tf.nextCell();
                    tf.addLine(String.valueOf(i + 1));
                }
                CarteExport carteExport = exportBooks.get(i);

                for (int j = 0; j < selectedFields.size(); j++) {
                    tf.nextCell();
                    Method method = ObjectUtil.getMethod(CarteExport.class, "get" + StringUtils.capitalize(selectedFields.get(j).getFieldName()));
                    String value = (String) method.invoke(carteExport, (Object[]) null);
                    tf.addLine(value);
                }
            }

            dlgClassic.close();
            String[] tbl = tf.getFormattedTable();

            logger.info("ExportTXT content to file : " + fileName);
            ps = new PrintStream(output);

            if (showTitle) {
                ps.println(titleName);
                ps.println();
            }

            for (int i = 0, size = tbl.length; i < size; i++) {
                ps.println("\t" + tbl[i]);
                if (showBorder) {
                    if (i == 2) {
                        ps.println();
                        i++;
                    }
                } else if (i == 0) {
                    ps.println();
                }
            }
            ps.println();
            ps.println("Raport generat cu Books Manager, https://github.com/brontozaur");
            ps.close();
            logger.info("ExportTXT content to file completed succesfully.");

            ApplicationReport dbRap = new ApplicationReport();
            dbRap.setCale(output.getCanonicalPath());
            dbRap.setIdUser(EncodeLive.getIdUser());
            dbRap.setNume(titleName);
            dbRap.setType(ExportType.TXT);

            ReportController.save(dbRap);

            VizualizareRapoarte.showRaport(dbRap);
        } catch (Exception exc) {
            if (dlgClassic != null) {
                dlgClassic.close();
            }
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("A intervenit o eroare la generarea fisierului.", exc);
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (dlgClassic != null) {
                dlgClassic.close();
            }
        }
    }

    private static int getAlign(final int swtConstant) {
        if (swtConstant == SWT.LEFT) {
            return TableFormatter.ALIGN_LEFT;
        } else if (swtConstant == SWT.RIGHT) {
            return TableFormatter.ALIGN_RIGHT;
        } else if (swtConstant == SWT.CENTER) {
            return TableFormatter.ALIGN_CENTER;
        }
        return TableFormatter.ALIGN_DEFAULT;
    }
}
