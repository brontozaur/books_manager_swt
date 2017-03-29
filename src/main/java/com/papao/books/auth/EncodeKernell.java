package com.papao.books.auth;

import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.perspective.WelcomePerspective;
import org.eclipse.swt.widgets.Shell;

public final class EncodeKernell extends EncodePlatform {

    public EncodeKernell() {
        super();
        CWaitDlgClassic dlg = new CWaitDlgClassic(10);
        dlg.setMessageLabel("Va rugam asteptati incarcarea aplicatiei");
        dlg.open();
        dlg.advance(5);
        getAppMainForm().setContent(new WelcomePerspective().getContent());
//        getShell().setMenuBar(PlatformMenu.createShellMenu(getShell()));
        dlg.close();
    }

    public static Shell getApplicationShell() {
        if (EncodePlatform.instance != null) {
            return EncodePlatform.instance.getShell();
        }
        return new Shell();
    }
}
