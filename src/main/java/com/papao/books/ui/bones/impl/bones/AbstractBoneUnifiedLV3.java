package com.papao.books.ui.bones.impl.bones;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.bones.AbstractBoneDescriptor;
import com.papao.books.ui.bones.filter.AbstractBoneFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AbstractBoneUnifiedLV3 extends AbstractBoneUnifiedLV2 {

    private ToolItem itemDetails;

    public AbstractBoneUnifiedLV3(final Composite parent,
                                  final String name,
                                  final Integer idBone,
                                  final AbstractBoneFilter filtru,
                                  final AbstractBoneDescriptor descriptor) {
        super(parent, name, idBone, filtru, descriptor);
    }

    public final void createToolItemShowDetails() {
        setItemDetails(new ToolItem(getBarOps(), SWT.CHECK));

        getItemDetails().setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
        getItemDetails().setHotImage(AppImages.getImage16Focus(AppImages.IMG_DETAILS_NEW));
        getItemDetails().setToolTipText("Afisare sau nu detalii pentru elementul selectat. Daca sunteti pozitionat in tabela, "
                + "apasati [Ctrl+E] pentru a schimba selectia.");
        getItemDetails().addListener(SWT.Selection, this);

        getItemDetails().addListener(SWT.Selection, this);
        if ((getFiltru() != null) && getFiltru().isBarOpsShowingText()) {
            getItemDetails().setText("  Detalii  ");
        }
    }

    /**
     * Method for handle SelectionEvents, callable from both SWT.Selection or SWT.KeyDown events
     * (convenient shortcut).
     * 
     * @param isCodeSelection
     *            a true value means it was called from SWT.KeyDown. This behaviour mimic
     *            setAccelerator() functionality from a shell Menu.
     */
    public final void handleRightCompDisplay(final boolean isCodeSelection) {
        if (isCodeSelection) {
            getItemDetails().setSelection(!getItemDetails().getSelection());
        }
        if (getItemDetails().getSelection()) {
            getRightInnerSash().setMaximizedControl(null);
            showDetails();
        } else {
            getRightInnerSash().setMaximizedControl(getInnerCompRight());
        }
    }

    public abstract void showDetails();

    @Override
    public void handleEvent(final Event e) {
        super.handleEvent(e);
        if ((e.type == SWT.Selection) && (e.widget == getItemDetails())) {
            handleRightCompDisplay(false);
        }
    }

    public final ToolItem getItemDetails() {
        return this.itemDetails;
    }

    private final void setItemDetails(final ToolItem itemDetails) {
        this.itemDetails = itemDetails;
    }
}
