package com.papao.books.view.custom;

import com.mongodb.gridfs.GridFSDBFile;
import com.papao.books.controller.AutorController;
import com.papao.books.model.Carte;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.FontUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class BookReadOnlyDetailsComposite {

    private Composite mainComp;
    private ScrolledComposite scrolledComposite;
    private AutorController autorController;

    private CLabel rightLabelTitle;
    private ImageSelectorComposite rightFrontCoverImageComposite;
    private LinkedInUrlsComposite rightWebResourcesComposite;
    private LinkedinCompositeAutoriLinks rightAutoriComposite;
    private LinkedInSimpleValuesComposite genLiterarComposite;
    private LinkedInSimpleValuesComposite taguriComposite;

    public BookReadOnlyDetailsComposite(Composite parent, AutorController autorController) {
        this.autorController = autorController;

        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
        mainComp = new Composite(scrolledComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).spacing(0, 0).applyTo(mainComp);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(mainComp);

        scrolledComposite.setContent(mainComp);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(mainComp.computeSize(r.width, SWT.DEFAULT));
            }
        });


        addComponents();
    }

    private void addComponents() {
        rightLabelTitle = new CLabel(mainComp, SWT.CENTER);
        rightLabelTitle.setFont(FontUtil.TAHOMA14_NORMAL);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(rightLabelTitle);
        GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 25).span(2, 1).applyTo(rightLabelTitle);
        rightLabelTitle.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
        rightLabelTitle.setForeground(ColorUtil.COLOR_WHITE);

        Composite temp = new Composite(mainComp, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(temp);
        GridDataFactory.fillDefaults().span(2, 1).align(SWT.CENTER, SWT.BEGINNING).applyTo(temp);
        rightFrontCoverImageComposite = new ImageSelectorComposite(temp, null, null, autorController.getAppImagesFolder());

        label("Web");
        rightWebResourcesComposite = new LinkedInUrlsComposite(mainComp, null);

        label(" Autori");
        rightAutoriComposite = new LinkedinCompositeAutoriLinks(mainComp, null, autorController);

        label("Gen", true);
        genLiterarComposite = new LinkedInSimpleValuesComposite(mainComp);

        label(" Taguri", true);
        taguriComposite = new LinkedInSimpleValuesComposite(mainComp);
    }

    public void populateFields(Carte carte) {
        if (carte.getTitlu().length() > 45) {
            rightLabelTitle.setText(carte.getTitlu().substring(0, 40) + "...");
        } else {
            rightLabelTitle.setText(carte.getTitlu());
        }
        rightLabelTitle.setToolTipText(carte.getTitlu());

        Image frontCover = null;
        String frontCoverImageName = null;
        if (carte.getCopertaFata() != null && carte.getCopertaFata().getId() != null) {
            GridFSDBFile dbFile = autorController.getDocumentData(carte.getCopertaFata().getId());
            if (dbFile != null) {
                frontCover = new Image(Display.getDefault(), dbFile.getInputStream());
                frontCoverImageName = dbFile.getFilename();
            }
        }
        rightFrontCoverImageComposite.setImage(frontCover, frontCoverImageName);
        rightWebResourcesComposite.setCarte(carte);
        rightAutoriComposite.setAutori(carte.getIdAutori());
        genLiterarComposite.setValues(carte.getGenLiterar());
        taguriComposite.setValues(carte.getTags());
        mainComp.pack();

        Rectangle r = scrolledComposite.getClientArea();
        scrolledComposite.setMinSize(mainComp.computeSize(r.width, SWT.DEFAULT));
    }

    private void label(String labelName) {
        label(labelName, false);
    }

    private void label(String labelName, boolean verticallyAlignBegining) {
        Label label = new Label(mainComp, SWT.NONE);
        label.setText(labelName);
        GridDataFactory.fillDefaults().align(SWT.END, verticallyAlignBegining ? SWT.BEGINNING : SWT.CENTER).applyTo(label);
    }
}
