package com.papao.books.view.custom;

import com.papao.books.controller.BookController;
import com.papao.books.controller.UserController;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ImageGalleryComposite extends Observable implements Observer {

    private BookController bookController;
    private UserController userController;
    private ScrolledComposite scrolledComposite;
    private Composite mainComp;
    private Carte selected;

    public ImageGalleryComposite(Composite parent, BookController bookController, UserController userController) {
        this.bookController = bookController;
        this.userController = userController;
        this.bookController.addObserver(this);

        scrolledComposite = new ScrolledComposite(parent, SWT.BORDER| SWT.V_SCROLL);
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
        mainComp.addListener(SWT.MouseEnter, new Listener() {
            public void handleEvent(Event e) {
                scrolledComposite.notifyListeners(SWT.Activate, new Event());
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BookController) {
            BookController controller = (BookController) o;
            populateFields(controller.getSearchResult().getContent());
        } else if (o instanceof ImageViewComposite) {
            this.selected = ((ImageViewComposite)o).getCarte();
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
    private void populateFields(List<Carte> carti) {
        CWaitDlgClassic dlg = new CWaitDlgClassic(carti.size());
        dlg.open();
        clearAll();
        for (Carte carte : carti) {
            dlg.advance();
            ImageViewComposite view = new ImageViewComposite(mainComp, bookController, userController, carte);
            view.addObserver(this);
        }
        mainComp.layout();
        scrolledComposite.notifyListeners(SWT.Resize, new Event());
        dlg.close();
    }

    public Composite getContent() {
        return this.scrolledComposite;
    }

    public Carte getSelected() {
        return selected;
    }
}
