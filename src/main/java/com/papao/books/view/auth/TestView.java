package com.papao.books.view.auth;

import com.papao.books.model.Carte;
import com.papao.books.repository.CarteRepository;
import com.papao.books.view.view.AbstractCView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestView extends AbstractCView {

    private CarteRepository carteRepository;

    @Autowired
    public TestView(final CarteRepository carteRepository) {
        super(null, MODE_VIEW);
        this.carteRepository = carteRepository;

        Button btn = new Button(getContainer(), SWT.PUSH);
        btn.setText("Create book");
        btn.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                carteRepository.save(new Carte());
            }
        });
    }

    @Override
    protected void customizeView() {
        setShellText("Test view");
    }

    @Override
    protected boolean validate() {
        return false;
    }

    @Override
    protected void saveData() {

    }
}
