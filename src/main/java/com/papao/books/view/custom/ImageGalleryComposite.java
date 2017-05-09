package com.papao.books.view.custom;

import com.papao.books.ApplicationService;
import com.papao.books.controller.BookController;
import com.papao.books.model.Carte;
import com.papao.books.view.util.ColorUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ImageGalleryComposite extends Observable implements Observer {

    private ScrolledComposite scrolledComposite;
    private Composite mainComp;
    private ProgressBarComposite progressBarComposite;
    private ImageViewComposite selected;
    private volatile boolean stop;

    public ImageGalleryComposite(Composite parent,
                                 ProgressBarComposite progressBarComposite) {
        this.progressBarComposite = progressBarComposite;
        ApplicationService.getBookController().addObserver(this);

        scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL);
        scrolledComposite.setLayout(new GridLayout());
        scrolledComposite.setBackground(ColorUtil.COLOR_WHITE);

        mainComp = new Composite(scrolledComposite, SWT.NONE);
        mainComp.setBackground(ColorUtil.COLOR_WHITE);
        RowLayoutFactory.swtDefaults().wrap(true).type(SWT.HORIZONTAL).applyTo(mainComp);
        GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.BEGINNING).applyTo(mainComp);

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
        scrolledComposite.addListener(SWT.Activate, new Listener() {
            public void handleEvent(Event e) {
                scrolledComposite.setFocus();
            }
        });
        mainComp.addListener(SWT.Activate, new Listener() {
            public void handleEvent(Event e) {
                scrolledComposite.notifyListeners(SWT.Activate, new Event());
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        stop = true;
        if (o instanceof BookController) {
            BookController controller = (BookController) o;
            populateFields(controller.getSearchResult().getContent());
        } else if (o instanceof ImageViewComposite) {
            if (this.selected != null) {
                this.selected.resetSelection(true);
            }
            this.selected = (ImageViewComposite) o;
            setChanged();
            notifyObservers();
        }
    }

    private void clearAll() {
        for (Control control : mainComp.getChildren()) {
            control.dispose();
        }
    }

    @Async
    private void populateFields(final List<Carte> carti) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (progressBarComposite.isDisposed()) {
                    return;
                }
                progressBarComposite.setMax(carti.size());
                clearAll();
                stop = false;
                for (Carte carte : carti) {
                    if (stop || progressBarComposite.isDisposed()) {
                        return;
                    }
                    progressBarComposite.advance();
                    ImageViewComposite view = new ImageViewComposite(mainComp, carte);
                    view.addObserver(ImageGalleryComposite.this);
                    mainComp.layout();
                    scrolledComposite.notifyListeners(SWT.Resize, new Event());
                    Display.getDefault().readAndDispatch();
                }
                stop = true;
                progressBarComposite.setMax(0);
            }
        };
        Display.getDefault().asyncExec(runnable);
    }

    public Composite getContent() {
        return this.scrolledComposite;
    }

    public Carte getSelected() {
        return selected != null ? selected.getCarte() : null;
    }
}
