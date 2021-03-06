package com.papao.books.ui.menu;

import com.papao.books.controller.SettingsController;
import com.papao.books.model.ImagePath;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.model.config.SearchEngine;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.util.ColorUtil;
import com.papao.books.ui.util.UrlImageValidator;
import com.papao.books.ui.util.WidgetCursorUtil;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.net.URL;

public final class WebBrowser extends AbstractCViewAdapter implements Listener {

    private static Logger logger = Logger.getLogger(WebBrowser.class);
    private ToolItem itemBack, itemNext, itemStop, itemRefresh, itemGo;
    private Combo comboAdress;
    private Combo comboSearchEngine;
    private ProgressBar pBar;
    private CLabel cLabelStatus;
    private static final String BACK = "back";
    private static final String FWD = "fwd";
    private static final String REFRESH = "refresh";
    private static final String STOP = "stop";
    private static final String GO = "go";
    private Browser browser;
    private ToolItem itemSave;
    private ImagePath imagePath;
    private String query;

    public WebBrowser(final Shell parent, String query) {
        super(parent, MODE_NONE);
        this.query = query;
        GeneralSetting searchEngineConfig = SettingsController.getGeneralSetting("searchEngine");
        int searchEngineIndex;
        if (searchEngineConfig != null) {
            searchEngineIndex = Integer.valueOf(searchEngineConfig.getValue().toString());
        } else {
            searchEngineIndex = SearchEngine.GOOGLE_COM.getOrdinal();
        }
        addComponents("", true);
        if (query != null) {
            comboSearchEngine.select(searchEngineIndex);
            comboSearchEngine.notifyListeners(SWT.Selection, new Event());
        }
    }

    /**
     * @param parent
     */
    public WebBrowser(final Shell parent, String startUrl, final boolean allowImageSelection) {
        super(parent, MODE_NONE);
        addComponents(startUrl, allowImageSelection);
    }

    private void addComponents(String startUrl, boolean allowImageSelection) {
        try {
            CLabel cLabelNavigare = new CLabel(getContainer(), SWT.SHADOW_ETCHED_OUT);
            cLabelNavigare.setLayout(new GridLayout(this.query != null ? 5 : 4, false));
            ((GridLayout) cLabelNavigare.getLayout()).verticalSpacing = 0;
            cLabelNavigare.setLayoutData(new GridData(
                    SWT.FILL,
                    SWT.FILL,
                    true,
                    false,
                    ((GridLayout) getContainer().getLayout()).numColumns,
                    1));
            ((GridData) cLabelNavigare.getLayoutData()).heightHint = 30;
            ((GridLayout) cLabelNavigare.getLayout()).marginHeight = ((GridLayout) cLabelNavigare.getLayout()).marginWidth = 1;
            ((GridLayout) cLabelNavigare.getLayout()).verticalSpacing = 0;

            ToolBar barNavigare = new ToolBar(cLabelNavigare, SWT.FLAT | SWT.WRAP);

            this.itemBack = new ToolItem(barNavigare, SWT.PUSH);
            this.itemBack.setImage(AppImages.getImage24(AppImages.IMG_ARROW_LEFT));
            this.itemBack.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_LEFT));
            this.itemBack.setData(WebBrowser.BACK);

            this.itemNext = new ToolItem(barNavigare, SWT.PUSH);
            this.itemNext.setImage(AppImages.getImage24(AppImages.IMG_ARROW_RIGHT));
            this.itemNext.setHotImage(AppImages.getImage24Focus(AppImages.IMG_ARROW_RIGHT));
            this.itemNext.setData(WebBrowser.FWD);

            this.itemRefresh = new ToolItem(barNavigare, SWT.PUSH);
            this.itemRefresh.setImage(AppImages.getImage24(AppImages.IMG_REFRESH));
            this.itemRefresh.setHotImage(AppImages.getImage24Focus(AppImages.IMG_REFRESH));
            this.itemRefresh.setData(WebBrowser.REFRESH);

            this.itemStop = new ToolItem(barNavigare, SWT.PUSH);
            this.itemStop.setImage(AppImages.getImage24(AppImages.IMG_STOP));
            this.itemStop.setHotImage(AppImages.getImage24Focus(AppImages.IMG_STOP));
            this.itemStop.setData(WebBrowser.STOP);

            new ToolItem(barNavigare, SWT.SEPARATOR);

            Label labelAdress = new Label(cLabelNavigare, SWT.NONE);
            labelAdress.setText("Adresa");

            this.comboAdress = new Combo(cLabelNavigare, SWT.BORDER | SWT.DROP_DOWN);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(comboAdress);
            this.comboAdress.setText(startUrl);
            SWTeXtension.addColoredFocusListener(this.comboAdress, ColorUtil.COLOR_FOCUS_YELLOW);
            this.comboAdress.addListener(SWT.Selection, this);

            if (this.query != null) {
                this.comboSearchEngine = new Combo(cLabelNavigare, SWT.READ_ONLY);
                this.comboSearchEngine.setItems(SearchEngine.getComboItems());
                GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).hint(120, SWT.DEFAULT).applyTo(comboSearchEngine);
                WidgetCursorUtil.addHandCursorListener(this.comboSearchEngine);
                comboSearchEngine.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        String startUrl = SearchEngine.getQueryByComboIndex(comboSearchEngine.getSelectionIndex());
                        startUrl = startUrl.replace(SearchEngine.QUERY_PLACEHOLDER, query);
                        setComboItems(startUrl);
                        browser.setUrl(startUrl);
                    }
                });
            }

            ToolBar barGo = new ToolBar(cLabelNavigare, SWT.FLAT | SWT.WRAP);

            this.itemGo = new ToolItem(barGo, SWT.PUSH);
            this.itemGo.setImage(AppImages.getImage24(AppImages.IMG_OK));
            this.itemGo.setHotImage(AppImages.getImage24Focus(AppImages.IMG_OK));
            this.itemGo.setData(WebBrowser.GO);

            int browser_style = SWT.BORDER;
            this.browser = new Browser(getContainer(), browser_style);
            this.browser.setLayoutData(new GridData(
                    SWT.FILL,
                    SWT.FILL,
                    true,
                    true,
                    ((GridLayout) getContainer().getLayout()).numColumns,
                    1));
            setComboItems(startUrl);
            this.browser.setUrl(startUrl);
            this.browser.addOpenWindowListener(new OpenWindowListener() {
                @Override
                public void open(WindowEvent event) {
                    event.browser = browser;
                }
            });

            Composite bottomComposite = new Composite(getContainer(), SWT.NONE);
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
            GridDataFactory.fillDefaults().align(SWT.END, SWT.END).applyTo(this.pBar);
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
                    getShell().setText(comboAdress.getText());
                    pBar.setSelection(0);
                    if (allowImageSelection) {
                        enableItemSave();
                    }
                    browser.setFocus();
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
                        setExitChoice(SWT.OK);
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

    public void resetResultSelection() {
        this.imagePath = null;
        setExitChoice(SWT.CANCEL);
    }

    public ImagePath getResult() {
        return this.imagePath;
    }

    private void saveAndClose() {
        if (getExitChoice() == SWT.OK) {
            imagePath = (ImagePath) itemSave.getData();
        } else {
            imagePath = null;
        }
        getShell().close();
    }

    private void enableItemSave() {
        try {
            if (!UrlImageValidator.validate(comboAdress.getText())) {
                itemSave.setEnabled(false);
                return;
            }
            URL url = new URL(comboAdress.getText());
            ImagePath path = new ImagePath();
            path.setFilePath(url.toString());
            path.setFileName(url.getFile().substring(url.getFile().lastIndexOf("/") + 1));
            itemSave.setData(path);
            itemSave.setEnabled(true);
        } catch (Exception exc) {
            itemSave.setEnabled(false);
            itemSave.setData(null);
            imagePath = null;
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

    private void navigate(final Event event) {
        try {
            ToolItem item = (ToolItem) event.widget;
            String string = item.getData().toString();
            switch (string) {
                case WebBrowser.BACK:
                    browser.back();
                    break;
                case WebBrowser.FWD:
                    browser.forward();
                    break;
                case WebBrowser.STOP:
                    browser.stop();
                    break;
                case WebBrowser.REFRESH:
                    browser.refresh();
                    break;
                case WebBrowser.GO:
                    setComboItems(this.comboAdress.getText());
                    browser.setUrl(this.comboAdress.getText());
                    break;
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    @Override
    public void handleEvent(final Event e) {
        try {
            switch (e.type) {
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

    @Override
    protected void customizeView() {
        setShellText("Web browser");
        setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE | SWT.MAX);
        setShellImage(AppImages.getImage16(AppImages.IMG_BROWSER));
        setBigViewImage(AppImages.getImage24(AppImages.IMG_BROWSER));
        setBigViewMessage("Web browser");

        org.eclipse.swt.graphics.Rectangle monitorBounds = Display.getDefault().getPrimaryMonitor().getBounds();
        final int monitorWidth = monitorBounds.width;
        final int monitorHeight = monitorBounds.height;
        setShellWidth(monitorWidth * 60 / 100);
        setShellHeight(monitorHeight * 60 / 100);
        setAddEscapeTraverseClose(true);
    }
}