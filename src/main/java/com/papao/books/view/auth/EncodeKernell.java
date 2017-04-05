package com.papao.books.view.auth;

import com.papao.books.view.EncodePlatform;
import com.papao.books.view.custom.CWaitDlgClassic;
import com.papao.books.view.perspective.WelcomePerspective;
import org.eclipse.swt.widgets.Shell;

public final class EncodeKernell extends EncodePlatform {

    public EncodeKernell() {
        super();
        CWaitDlgClassic dlg = new CWaitDlgClassic(10);
        dlg.setMessageLabel("Va rugam asteptati incarcarea aplicatiei");
        dlg.open();
        dlg.advance(5);
        getAppMainForm().setContent(new WelcomePerspective(this.getAppMainForm()).getContent());
//        getShell().setMenuBar(PlatformMenu.createShellMenu(getShell()));
        dlg.close();
    }

    public static Shell getApplicationShell() {
        if (EncodePlatform.getInstance() != null) {
            return EncodePlatform.getInstance().getShell();
        }
        return new Shell();
    }
}
