package com.papao.books.view.menu;

import com.papao.books.view.AppImages;
import com.papao.books.view.util.ColorUtil;
import com.papao.books.view.util.WidgetCompositeUtil;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
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
    private static Browser browser = null;
    private Shell shell;
    private Map<String, TreeItem> locations = new LinkedHashMap<String, TreeItem>();

    /**
     * @param parent
     * @param USE_MOZILLA
     *            , we'll try to create a Browser with SWT.MOZILLA param..throws SWTError if
     *            XulRunner is not found, and no handle could be found. Xul installation procedure :
     *            <p>
     *            1. Expand the xulrunner-1.8.0.4.en-US.win32.zip to a dir of your choice
     *            </p>
     *            <p>
     *            2. Run command : xulrunner.exe --register-global.
     *            </p>
     *            <p>
     *            3. Enjoy the newly Mozilla flavoured browser!
     *            </p>
     */
    public HelpBrowser(final Shell parent, String url, final boolean USE_MOZILLA) {
        int width;
        int height;
        Composite bigUpperComp;
        GridData data;
        Label bigLabelText;
        Label bigLabelImage;
        Label separator;
        try {
            if (parent != null) {
                setShell(new Shell(parent, SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE));
            } else {
                setShell(new Shell(Display.getDefault(), SWT.MIN | SWT.MAX | SWT.CLOSE | SWT.RESIZE));
            }
            getShell().setLayout(new GridLayout(1, false));
            ((GridLayout) getShell().getLayout()).marginHeight = 0;
            ((GridLayout) getShell().getLayout()).marginWidth = 0;
            ((GridLayout) getShell().getLayout()).verticalSpacing = 0;
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
            getShell().setSize(width, height);
            getShell().setImage(AppImages.getImage16(AppImages.IMG_HELP));
            getShell().setText("Help");
            getShell().addListener(SWT.Traverse, this);

            bigUpperComp = new Composite(getShell(), SWT.NONE);
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

            separator = new Label(getShell(), SWT.SEPARATOR | SWT.HORIZONTAL);
            data = new GridData(SWT.FILL, SWT.END, true, false);
            data.horizontalSpan = ((GridLayout) getShell().getLayout()).numColumns;
            separator.setLayoutData(data);

            WidgetCompositeUtil.centerInDisplay(getShell());
            setCompBrowser(new Composite(getShell(), SWT.NONE));
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            getCompBrowser().setLayoutData(gd);
            getCompBrowser().setLayout(new GridLayout(2, false));
            ((GridLayout) getCompBrowser().getLayout()).verticalSpacing = 0;
            SWTeXtension.addToolTipListener(getCompBrowser(), "Web browser");
            getCompBrowser().addListener(SWT.Paint, this);

            setCLabelNavigare(new CLabel(getCompBrowser(), SWT.SHADOW_ETCHED_OUT));
            getCLabelNavigare().setLayout(new GridLayout(4, false));
            ((GridLayout) getCLabelNavigare().getLayout()).verticalSpacing = 0;
            getCLabelNavigare().setLayoutData(new GridData(
                SWT.FILL,
                SWT.FILL,
                true,
                false,
                ((GridLayout) getCompBrowser().getLayout()).numColumns,
                1));
            ((GridData) getCLabelNavigare().getLayoutData()).heightHint = 25;
            ((GridLayout) getCLabelNavigare().getLayout()).marginHeight = ((GridLayout) getCLabelNavigare().getLayout()).marginWidth = 1;
            ((GridLayout) getCLabelNavigare().getLayout()).verticalSpacing = 0;

            setBarNavigare(new ToolBar(getCLabelNavigare(), SWT.FLAT | SWT.WRAP));
            gd = new GridData(GridData.FILL_HORIZONTAL);

            setItemBack(new ToolItem(getBarNavigare(), SWT.PUSH));
            getItemBack().setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT));
            getItemBack().setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_LEFT));
            getItemBack().setData(HelpBrowser.BACK);

            setItemNext(new ToolItem(getBarNavigare(), SWT.PUSH));
            getItemNext().setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
            getItemNext().setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT));
            getItemNext().setData(HelpBrowser.FWD);

            setItemRefresh(new ToolItem(getBarNavigare(), SWT.PUSH));
            getItemRefresh().setImage(AppImages.getImage16(AppImages.IMG_REFRESH));
            getItemRefresh().setHotImage(AppImages.getImage16Focus(AppImages.IMG_REFRESH));
            getItemRefresh().setData(HelpBrowser.REFRESH);

            setItemStop(new ToolItem(getBarNavigare(), SWT.PUSH));
            getItemStop().setImage(AppImages.getImage16(AppImages.IMG_STOP));
            getItemStop().setHotImage(AppImages.getImage16Focus(AppImages.IMG_STOP));
            getItemStop().setData(HelpBrowser.STOP);

            new ToolItem(getBarNavigare(), SWT.SEPARATOR);

            setLabelAdress(new Label(getCLabelNavigare(), SWT.NONE));
            getLabelAdress().setText("Adresa");

            setComboAdress(new Combo(getCLabelNavigare(), SWT.BORDER | SWT.DROP_DOWN));
            gd = new GridData(GridData.FILL_HORIZONTAL);
            getComboAdress().setLayoutData(gd);
            getComboAdress().setText(url);
            SWTeXtension.addColoredFocusListener(getComboAdress(), ColorUtil.COLOR_FOCUS_YELLOW);
            getComboAdress().addListener(SWT.Selection, this);

            setBarGo(new ToolBar(getCLabelNavigare(), SWT.FLAT | SWT.WRAP));

            setItemGo(new ToolItem(getBarGo(), SWT.PUSH));
            getItemGo().setImage(AppImages.getImage16(AppImages.IMG_OK));
            getItemGo().setHotImage(AppImages.getImage16Focus(AppImages.IMG_OK));
            getItemGo().setData(HelpBrowser.GO);

            try {
                int browser_style = SWT.BORDER;
                if (USE_MOZILLA) {
                    browser_style |= SWT.MOZILLA;
                }
                HelpBrowser.setBrowser(new Browser(getCompBrowser(), browser_style));
                HelpBrowser.getBrowser().setLayoutData(new GridData(
                    SWT.FILL,
                    SWT.FILL,
                    true,
                    true,
                    ((GridLayout) getCompBrowser().getLayout()).numColumns,
                    1));
            } catch (SWTError exc) {
				logger.error(exc.getMessage(), exc);
            }
            if (HelpBrowser.getBrowser() != null) {
                setComboItems(url);
                HelpBrowser.getBrowser().setUrl(url);
            }

            setCLabelStatus(new CLabel(getCompBrowser(), SWT.BORDER));
            getCLabelStatus().setLayout(new GridLayout(3, false));
            ((GridLayout) getCLabelStatus().getLayout()).verticalSpacing = 0;
            getCLabelStatus().setLayoutData(new GridData(
                SWT.FILL,
                SWT.FILL,
                true,
                false,
                ((GridLayout) getCompBrowser().getLayout()).numColumns - 1,
                1));
            ((GridData) getCLabelStatus().getLayoutData()).heightHint = 20;
            ((GridLayout) getCLabelStatus().getLayout()).marginHeight = ((GridLayout) getCLabelStatus().getLayout()).marginWidth = 1;
            ((GridLayout) getCLabelStatus().getLayout()).verticalSpacing = 0;
            getCLabelStatus().setBackground(new Color[] {
                    ColorUtil.COLOR_FOCUS_YELLOW, ColorUtil.COLOR_WHITE }, new int[] {
                100 });
            getCLabelStatus().addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(final DisposeEvent event) {
                    CLabel cl = (CLabel) event.widget;
                    if ((cl.getBackground() != null) && !cl.getBackground().isDisposed()) {
                        cl.getBackground().dispose();
                    }
                }
            });

            setPBar(new ProgressBar(getCompBrowser(), SWT.NONE));
            gd = new GridData();
            gd.horizontalAlignment = GridData.END;
            getPBar().setLayoutData(gd);
            getPBar().setForeground(ColorUtil.COLOR_FOCUS_YELLOW);

            HelpBrowser.getBrowser().addProgressListener(new ProgressListener() {
                @Override
                public void changed(final ProgressEvent event) {
                    if (event.total == 0) {
                        return;
                    }
                    int ratio = event.current * 100 / event.total;
                    getPBar().setSelection(ratio);
                }

                @Override
                public void completed(final ProgressEvent event) {
                    getPBar().setSelection(0);
                }
            });
            HelpBrowser.getBrowser().addStatusTextListener(new StatusTextListener() {
                @Override
                public void changed(final StatusTextEvent event) {
                    getCLabelStatus().setText(event.text);
                }
            });
            HelpBrowser.getBrowser().addLocationListener(new LocationAdapter() {
                @Override
                public void changed(final LocationEvent event) {
                    if (event.top) {
                        getComboAdress().setText(event.location);
                    }
                    setComboItems(event.location);
                    getItemBack().setEnabled(((Browser) event.widget).isBackEnabled());
                    getItemNext().setEnabled(((Browser) event.widget).isForwardEnabled());
                }
            });
            getItemBack().addListener(SWT.Selection, this);
            getItemNext().addListener(SWT.Selection, this);
            getItemStop().addListener(SWT.Selection, this);
            getItemRefresh().addListener(SWT.Selection, this);
            getItemGo().addListener(SWT.Selection, this);

            getComboAdress().addListener(SWT.DefaultSelection, this);
        } catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
        }
    }

    private void setComboItems(final String str) {
        try {
            String[] items = getComboAdress().getItems();
            if (items.length == 0) {
                getComboAdress().add(str);
                return;
            }
            if (getComboAdress().indexOf(str) == -1) {
                getComboAdress().add(str);
            }
            if (getComboAdress().getItemCount() == 50) {
                getComboAdress().removeAll();
                getComboAdress().add(str);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public void open() {
        Display d = null;
        try {
            d = getShell().getDisplay();
            getShell().open();
            while ((getShell() != null) && !getShell().isDisposed()) {
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

    public Composite getCompBrowser() {
        return this.compBrowser;
    }

    public void setCompBrowser(final Composite compBrowser) {
        this.compBrowser = compBrowser;
    }

    public static void setBrowser(final Browser browser) {
        HelpBrowser.browser = browser;
    }

    public static Browser getBrowser() {
        return HelpBrowser.browser;
    }

    private ToolBar getBarNavigare() {
        return this.barNavigare;
    }

    private void setBarNavigare(final ToolBar barNavigare) {
        this.barNavigare = barNavigare;
    }

    private ToolItem getItemBack() {
        return this.itemBack;
    }

    private void setItemBack(final ToolItem itemBack) {
        this.itemBack = itemBack;
    }

    private ToolItem getItemNext() {
        return this.itemNext;
    }

    private void setItemNext(final ToolItem itemNext) {
        this.itemNext = itemNext;
    }

    private ToolItem getItemStop() {
        return this.itemStop;
    }

    private void setItemStop(final ToolItem itemStop) {
        this.itemStop = itemStop;
    }

    private ToolItem getItemRefresh() {
        return this.itemRefresh;
    }

    private void setItemRefresh(final ToolItem itemRefresh) {
        this.itemRefresh = itemRefresh;
    }

    private ToolItem getItemGo() {
        return this.itemGo;
    }

    private void setItemGo(final ToolItem itemGo) {
        this.itemGo = itemGo;
    }

    private Label getLabelAdress() {
        return this.labelAdress;
    }

    private void setLabelAdress(final Label labelAdress) {
        this.labelAdress = labelAdress;
    }

    private Combo getComboAdress() {
        return this.comboAdress;
    }

    private void setComboAdress(final Combo comboAdress) {
        this.comboAdress = comboAdress;
    }

    private ProgressBar getPBar() {
        return this.pBar;
    }

    private void setPBar(final ProgressBar bar) {
        this.pBar = bar;
    }

    private CLabel getCLabelNavigare() {
        return this.cLabelNavigare;
    }

    private void setCLabelNavigare(final CLabel labelNavigare) {
        this.cLabelNavigare = labelNavigare;
    }

    private CLabel getCLabelStatus() {
        return this.cLabelStatus;
    }

    private void setCLabelStatus(final CLabel labelStatus) {
        this.cLabelStatus = labelStatus;
    }

    private ToolBar getBarGo() {
        return this.barGo;
    }

    private void setBarGo(final ToolBar barGo) {
        this.barGo = barGo;
    }

    public void setLocations(final Map<String, TreeItem> locations) {
        this.locations = locations;
    }

    public Map<String, TreeItem> getLocations() {
        return this.locations;
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
                HelpBrowser.browser.back();
            } else if (string.equals(HelpBrowser.FWD)) {
                HelpBrowser.browser.forward();
            } else if (string.equals(HelpBrowser.STOP)) {
                HelpBrowser.browser.stop();
            } else if (string.equals(HelpBrowser.REFRESH)) {
                HelpBrowser.browser.refresh();
            } else if (string.equals(HelpBrowser.GO)) {
                setComboItems(getComboAdress().getText());
                HelpBrowser.browser.setUrl(getComboAdress().getText());
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
                    if (e.widget == getShell()) {
                        if (e.detail == SWT.TRAVERSE_ESCAPE) {
                            getShell().close();
                            e.detail = SWT.TRAVERSE_NONE;
                            e.doit = true;
                        }
                    }
                    break;
                }
                case SWT.Paint: {
                    if (e.widget == getCompBrowser()) {
                        GC gc = e.gc;
                        gc.setForeground(ColorUtil.COLOR_ALBASTRU_INCHIS);
                        Rectangle rect = getCompBrowser().getClientArea();
                        gc.drawRectangle(rect.x, rect.y + 1, rect.width - 1, rect.height - 3);
                        gc.dispose();
                    }
                    break;
                }
                case SWT.Selection: {
                    if (e.widget == getComboAdress()) {
                        HelpBrowser.getBrowser().setUrl(getComboAdress().getText());
                    } else if (e.widget == getItemBack()) {
                        navigate(e);
                    } else if (e.widget == getItemGo()) {
                        navigate(e);
                    } else if (e.widget == getItemNext()) {
                        navigate(e);
                    } else if (e.widget == getItemRefresh()) {
                        navigate(e);
                    } else if (e.widget == getItemStop()) {
                        navigate(e);
                    }
                    break;
                }
                case SWT.DefaultSelection: {
                    if (e.widget == getComboAdress()) {
                        setComboItems(getComboAdress().getText());
                        HelpBrowser.browser.setUrl(getComboAdress().getText());
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