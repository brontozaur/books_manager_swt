package com.papao.books.view.menu;

import com.papao.books.model.ImagePath;
import com.papao.books.view.AppImages;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import sun.awt.image.URLImageSource;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HelpBrowser implements Listener {

    private static Logger logger = Logger.getLogger(HelpBrowser.class);
    private Composite compBrowser;
    private ToolBar barNavigare, barGo;
    private ToolItem itemBack, itemNext, itemStop, itemRefresh, itemGo;
    private Label labelAdress;
    private Combo comboAdress;
    private ProgressBar pBar;
    private CLabel cLabelNavigare, cLabelStatus;
    private static final String BACK = "back";
    private static final String FWD = "fwd";
    private static final String REFRESH = "refresh";
    private static final String STOP = "stop";
    private static final String GO = "go";
    private Browser browser;
    private Shell shell;
    private Map<String, TreeItem> locations = new LinkedHashMap<String, TreeItem>();
    private ToolItem itemSave;
    private ImagePath imagePath;

    /**
     * @param parent
     */
    public HelpBrowser(final Shell parent, String startUrl, final boolean allowImageSelection) {
        int width;
        int height;
        Composite bigUpperComp;
        GridData data;
        Label bigLabelText;
        Label bigLabelImage;
        Label separator;
        try {
            if (parent != null) {
                this.shell = new Shell(parent, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
            } else {
                this.shell = new Shell(Display.getDefault(), SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE);
            }
            this.shell.setLayout(new GridLayout(1, false));
            ((GridLayout) this.shell.getLayout()).marginHeight = 0;
            ((GridLayout) this.shell.getLayout()).marginWidth = 0;
            ((GridLayout) this.shell.getLayout()).verticalSpacing = 0;
            if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1024) {
                width = 800;
            } else {
                width = 640;
            }
            if (Display.getDefault().getPrimaryMonitor().getBounds().height > 768) {
                height = 600;
            } else {
                height = 480;
            }
            this.shell.setSize(width, height);
            this.shell.setImage(AppImages.getImage16(AppImages.IMG_HELP));
            this.shell.setText("Help");
            this.shell.addListener(SWT.Traverse, this);

            bigUpperComp = new Composite(this.shell, SWT.NONE);
            GridLayout lay = new GridLayout(2, false);
            lay.marginWidth = 0;
            lay.marginLeft = 10;
            lay.marginRight = 5;
            bigUpperComp.setLayout(lay);
            data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            bigUpperComp.setLayoutData(data);
            bigUpperComp.setBackground(ColorUtil.COLOR_ALBASTRU_DESCHIS);
            bigUpperComp.setBackgroundMode(SWT.INHERIT_FORCE);

            bigLabelText = new Label(bigUpperComp, SWT.NONE);
            data = new GridData(SWT.FILL, SWT.CENTER, true, true);
            bigLabelText.setLayoutData(data);
            bigLabelText.setText("Web browser integrat");

            bigLabelImage = new Label(bigUpperComp, SWT.NONE);
            data = new GridData(SWT.END, SWT.FILL, false, true);
            bigLabelImage.setLayoutData(data);
            bigLabelImage.setImage(AppImages.getImage24(AppImages.IMG_HELP));

            separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
            data = new GridData(SWT.FILL, SWT.END, true, false);
            data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
            separator.setLayoutData(data);

            WidgetCompositeUtil.centerInDisplay(this.shell);
            this.compBrowser = new Composite(this.shell, SWT.NONE);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            this.compBrowser.setLayoutData(gd);
            this.compBrowser.setLayout(new GridLayout(2, false));
            ((GridLayout) this.compBrowser.getLayout()).verticalSpacing = 0;
            SWTeXtension.addToolTipListener(this.compBrowser, "Web browser");
            this.compBrowser.addListener(SWT.Paint, this);

            this.cLabelNavigare = new CLabel(this.compBrowser, SWT.SHADOW_ETCHED_OUT);
            this.cLabelNavigare.setLayout(new GridLayout(4, false));
            ((GridLayout) this.cLabelNavigare.getLayout()).verticalSpacing = 0;
            this.cLabelNavigare.setLayoutData(new GridData(
                    SWT.FILL,
                    SWT.FILL,
                    true,
                    false,
                    ((GridLayout) this.compBrowser.getLayout()).numColumns,
                    1));
            ((GridData) this.cLabelNavigare.getLayoutData()).heightHint = 25;
            ((GridLayout) this.cLabelNavigare.getLayout()).marginHeight = ((GridLayout) this.cLabelNavigare.getLayout()).marginWidth = 1;
            ((GridLayout) this.cLabelNavigare.getLayout()).verticalSpacing = 0;

            this.barNavigare = new ToolBar(this.cLabelNavigare, SWT.FLAT | SWT.WRAP);
            gd = new GridData(GridData.FILL_HORIZONTAL);

            this.itemBack = new ToolItem(this.barNavigare, SWT.PUSH);
            this.itemBack.setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT));
            this.itemBack.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_LEFT));
            this.itemBack.setData(HelpBrowser.BACK);

            this.itemNext = new ToolItem(this.barNavigare, SWT.PUSH);
            this.itemNext.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
            this.itemNext.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT));
            this.itemNext.setData(HelpBrowser.FWD);

            this.itemRefresh = new ToolItem(this.barNavigare, SWT.PUSH);
            this.itemRefresh.setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
            this.itemRefresh.setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
            this.itemRefresh.setData(HelpBrowser.REFRESH);

            this.itemStop = new ToolItem(this.barNavigare, SWT.PUSH);
            this.itemStop.setImage(AppImages.getImage16(AppImages.IMG_STOP));
            this.itemStop.setHotImage(AppImages.getImage16Focus(AppImages.IMG_STOP));
            this.itemStop.setData(HelpBrowser.STOP);

            new ToolItem(this.barNavigare, SWT.SEPARATOR);

            this.labelAdress = new Label(this.cLabelNavigare, SWT.NONE);
            this.labelAdress.setText("Adresa");

            this.comboAdress = new Combo(this.cLabelNavigare, SWT.BORDER | SWT.DROP_DOWN);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            this.comboAdress.setLayoutData(gd);
            this.comboAdress.setText(startUrl);
            SWTeXtension.addColoredFocusListener(this.comboAdress, ColorUtil.COLOR_FOCUS_YELLOW);
            this.comboAdress.addListener(SWT.Selection, this);

            this.barGo = new ToolBar(this.cLabelNavigare, SWT.FLAT | SWT.WRAP);

            this.itemGo = new ToolItem(this.barGo, SWT.PUSH);
            this.itemGo.setImage(AppImages.getImage16(AppImages.IMG_OK));
            this.itemGo.setHotImage(AppImages.getImage16Focus(AppImages.IMG_OK));
            this.itemGo.setData(HelpBrowser.GO);

            int browser_style = SWT.BORDER;
            this.browser = new Browser(this.compBrowser, browser_style);
            this.browser.setLayoutData(new GridData(
                    SWT.FILL,
                    SWT.FILL,
                    true,
                    true,
                    ((GridLayout) this.compBrowser.getLayout()).numColumns,
                    1));
            setComboItems(startUrl);
            this.browser.setUrl(startUrl);
            this.browser.addOpenWindowListener(new OpenWindowListener() {
                @Override
                public void open(WindowEvent event) {
                    event.browser = browser;
                }
            });

            Composite bottomComposite = new Composite(compBrowser, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(bottomComposite);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.END).applyTo(bottomComposite);

            cLabelStatus = new CLabel(bottomComposite, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(cLabelStatus);
            GridLayoutFactory.fillDefaults().spacing(0, 0).margins(0, 0).applyTo(cLabelStatus);
            cLabelStatus.setBackground(new Color[]{
                    ColorUtil.COLOR_FOCUS_YELLOW, ColorUtil.COLOR_WHITE}, new int[]{
                    100});
            cLabelStatus.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(final DisposeEvent event) {
                    CLabel cl = (CLabel) event.widget;
                    if ((cl.getBackground() != null) && !cl.getBackground().isDisposed()) {
                        cl.getBackground().dispose();
                    }
                }
            });

            this.pBar = new ProgressBar(bottomComposite, SWT.NONE);
            gd = new GridData();
            gd.horizontalAlignment = GridData.END;
            this.pBar.setLayoutData(gd);
            this.pBar.setForeground(ColorUtil.COLOR_FOCUS_YELLOW);

            this.browser.addProgressListener(new ProgressListener() {
                @Override
                public void changed(final ProgressEvent event) {
                    if (event.total == 0 || event.current == 0
                            || event.total == event.current) {
                        pBar.setSelection(0);
                        return;
                    }
                    int ratio = event.current * 100 / event.total;
                    pBar.setSelection(ratio);
                }

                @Override
                public void completed(final ProgressEvent event) {
                    pBar.setSelection(0);
                    if (allowImageSelection) {
                        enableItemSave();
                    }
                }
            });
            this.browser.addStatusTextListener(new StatusTextListener() {
                @Override
                public void changed(final StatusTextEvent event) {
                    cLabelStatus.setText(event.text);
                }
            });
            browser.addLocationListener(new LocationAdapter() {
                @Override
                public void changed(final LocationEvent event) {
                    if (event.top) {
                        comboAdress.setText(event.location);
                    }
                    setComboItems(event.location);
                    itemBack.setEnabled(((Browser) event.widget).isBackEnabled());
                    itemNext.setEnabled(((Browser) event.widget).isForwardEnabled());
                }
            });

            if (allowImageSelection) {
                itemSave = new ToolItem(new ToolBar(bottomComposite, SWT.FLAT | SWT.RIGHT), SWT.NONE);
                itemSave.setImage(AppImages.getImage16(AppImages.IMG_OK));
                itemSave.setHotImage(AppImages.getImage16(AppImages.IMG_OK));
                itemSave.setText("Selectare imagine");
                itemSave.setEnabled(false);
                itemSave.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        saveAndClose();
                    }
                });
            }

            this.itemBack.addListener(SWT.Selection, this);
            this.itemNext.addListener(SWT.Selection, this);
            this.itemStop.addListener(SWT.Selection, this);
            this.itemRefresh.addListener(SWT.Selection, this);
            this.itemGo.addListener(SWT.Selection, this);
            this.comboAdress.addListener(SWT.DefaultSelection, this);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public ImagePath getResult() {
        return this.imagePath;
    }

    private void saveAndClose() {
        imagePath = (ImagePath) itemSave.getData();
        getShell().close();
    }

    private void enableItemSave() {
        try {
            URL url = new URL(comboAdress.getText());
            if (!(url.getContent() instanceof URLImageSource)) {
                return;
            }
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            ImagePath path = new ImagePath();
            path.setFilePath(url.toString());
            path.setFileName(url.getFile().substring(url.getFile().lastIndexOf("/") + 1));
            itemSave.setData(path);
            itemSave.setEnabled(true);
        } catch (Exception exc) {
            itemSave.setEnabled(false);
        }
    }

    private void setComboItems(final String str) {
        try {
            String[] items = this.comboAdress.getItems();
            if (items.length == 0) {
                this.comboAdress.add(str);
                return;
            }
            if (this.comboAdress.indexOf(str) == -1) {
                this.comboAdress.add(str);
            }
            if (this.comboAdress.getItemCount() == 50) {
                this.comboAdress.removeAll();
                this.comboAdress.add(str);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public void open() {
        Display d = null;
        try {
            d = this.shell.getDisplay();
            this.shell.open();
            while ((this.shell != null) && !this.shell.isDisposed()) {
                if ((d != null) && !d.readAndDispatch()) {
                    d.sleep();
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        } finally {
            if (d != null) {
                d = null;
            }
        }
    }


    public Shell getShell() {
        return this.shell;
    }

    public void setShell(final Shell shell) {
        this.shell = shell;
    }

    private void navigate(final Event event) {
        try {
            ToolItem item = (ToolItem) event.widget;
            String string = item.getData().toString();
            if (string.equals(HelpBrowser.BACK)) {
                browser.back();
            } else if (string.equals(HelpBrowser.FWD)) {
                browser.forward();
            } else if (string.equals(HelpBrowser.STOP)) {
                browser.stop();
            } else if (string.equals(HelpBrowser.REFRESH)) {
                browser.refresh();
            } else if (string.equals(HelpBrowser.GO)) {
                setComboItems(this.comboAdress.getText());
                browser.setUrl(this.comboAdress.getText());
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    @Override
    public void handleEvent(final Event e) {
        try {
            switch (e.type) {
                case SWT.Traverse: {
                    if (e.widget == this.shell) {
                        if (e.detail == SWT.TRAVERSE_ESCAPE) {
                            this.shell.close();
                            e.detail = SWT.TRAVERSE_NONE;
                            e.doit = true;
                        }
                    }
                    break;
                }
                case SWT.Paint: {
                    if (e.widget == this.compBrowser) {
                        GC gc = e.gc;
                        gc.setForeground(ColorUtil.COLOR_ALBASTRU_INCHIS);
                        Rectangle rect = this.compBrowser.getClientArea();
                        gc.drawRectangle(rect.x, rect.y + 1, rect.width - 1, rect.height - 3);
                        gc.dispose();
                    }
                    break;
                }
                case SWT.Selection: {
                    if (e.widget == this.comboAdress) {
                        browser.setUrl(this.comboAdress.getText());
                    } else if (e.widget == this.itemBack) {
                        navigate(e);
                    } else if (e.widget == this.itemGo) {
                        navigate(e);
                    } else if (e.widget == this.itemNext) {
                        navigate(e);
                    } else if (e.widget == this.itemRefresh) {
                        navigate(e);
                    } else if (e.widget == this.itemStop) {
                        navigate(e);
                    }
                    break;
                }
                case SWT.DefaultSelection: {
                    if (e.widget == this.comboAdress) {
                        setComboItems(this.comboAdress.getText());
                        browser.setUrl(this.comboAdress.getText());
                    }
                    break;
                }
                default:
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }
}