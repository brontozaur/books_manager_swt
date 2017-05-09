package com.papao.books.ui.menu;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.util.*;
import com.papao.books.ui.view.AbstractCViewAdapter;
import com.papao.books.ui.view.AbstractView;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public final class CreditsView extends AbstractCViewAdapter implements Runnable {

	private static Logger logger = Logger.getLogger(CreditsView.class);

	private Composite compSplash, compCredits;
	private CTabFolder folder;
	private ScrolledComposite compVersions;
	private int sizeX;
	private int sizeY;
	private final List<String> arrayCredits = new ArrayList<String>();
	private final static String ENCODE = "\r\n\tENCODE SYSTEMS  ";
	public final static String SEPARATOR = "\r\n\t";
	private final static String credits = "Octavian Popa \r\n\t lead programmer\r\n\t"
			+ "Soft dezvoltat in \r\n\t Java 1.6\r\n\t"
			+ "Copyright \r\n\t\u00A9Sun Microsystems\r\n\t"
			+ "Soft dezvoltat utilizand \r\n\t SWT 3.4\u00A9 IBM\r\n\t"
			+ "Soft dezvoltat utilizand \r\n\t IntelliJ Idea\u00A9 Jet Brains\r\n\t"
			+ "Greetings go out to : \r\n\t \r\n\t" + "intelliJ \r\n\t manny thanks\r\n\t"
			+ "and all of you \r\n\twho inspired us all.\r\n\t"
			+ "\r\n\tBooks manager is a dream\r\n\t come true.\r\n\t ";
	private StyledText styledTextCredits;
	private StyledText styledTextVersiuni;
	private static final Font FONT_CREDITS = new Font(
		Display.getDefault(),
		"Courier New",
		12,
		SWT.NORMAL);
	private AnnimationThread annimationThread;
	private static final String[] tabNames = new String[] {
			"Aplicatie", "Sistem", "Setari", "Resurse", "Versiuni", "Credits" };

	private final static int IDX_APLICATIE = 0;
	private final static int IDX_SISTEM = 1;
	private final static int IDX_SETARI = 2;
	private final static int IDX_RESURSE = 3;
	private final static int IDX_VERSIUNI = 4;
	private final static int IDX_CREDITS = 5;

	private Table tableSysProps, tableLocalSettings;

	private Composite compResurse;

	public CreditsView(final Shell shell) {
		super(shell, AbstractView.MODE_NONE);
	}

	@Override
	public void customizeView() {
		setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE/* | SWT.APPLICATION_MODAL */);
		setViewOptions(AbstractView.ADD_CANCEL);
		setShellImage(AppImages.getImage16(AppImages.IMG_HOME));
		setShellWidth(430);
		setShellHeight(600);
		setBigViewImage(AppImages.getImage24(AppImages.IMG_HOME));
		setBigViewMessage("Despre " + Constants.ENCODE_SHELL_TEXT);
	}

	public void addComponents() {
		GridData gdata;
		try {
			getShell().setText("Despre");
			getShell().addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent e) {
					CreditsView.this.arrayCredits.clear();
					if (getAnnimationThread() != null) {
						getAnnimationThread().terminate();
					}
				}
			});

			this.sizeX = getShell().getClientArea().width;
			this.sizeY = getShell().getClientArea().height / 2;

			setFolder(new CTabFolder(getContainer(), SWT.NONE));
			gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
			gdata.widthHint = this.sizeX;
			gdata.heightHint = this.sizeY;
			getFolder().setLayoutData(gdata);
			getFolder().setSimple(true);
			getFolder().setUnselectedImageVisible(false);
			getFolder().setUnselectedCloseVisible(false);
			getFolder().setMRUVisible(true);

			getFolder().setMinimizeVisible(false);
			getFolder().setMaximizeVisible(false);
			getFolder().setSelection(0);

			getFolder().addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(final Event e) {
					if (e.widget.isDisposed()) {
						return;
					}
					if ((getFolder().getItemCount() == 0)
							|| (getFolder().getSelectionIndex() == -1)) {
						return;
					}

					switch (getFolder().getSelectionIndex()) {
						case IDX_APLICATIE: {
							updateDetailMessage("Sigla firma producatoare si numarul versiunii curente.");
							break;
						}
						case IDX_CREDITS: {
							updateDetailMessage("O scurta cuvantare vizavi de autori si aplicatie");
							break;
						}
						case IDX_RESURSE: {
							updateDetailMessage("Resursele alocate aplicatiei, in acest moment");
							break;
						}
						case IDX_SETARI: {
							updateDetailMessage("Setarile de baza ale utilizatorului curent");
							break;
						}
						case IDX_SISTEM: {
							updateDetailMessage("Setarile masinii virtuale de pe calculatorul curent");
							break;
						}
						case IDX_VERSIUNI: {
							updateDetailMessage("Versiuni aplicatie si modificarile lor");
							break;
						}
						default:
							updateDetailMessage("");
					}

					CTabItem item = getFolder().getSelection();
					if ((item != null) && !item.isDisposed()) {
						if (item.getText().equalsIgnoreCase(CreditsView.tabNames[CreditsView.IDX_CREDITS])) {
							CreditsView.this.arrayCredits.clear();
							CreditsView.this.arrayCredits.addAll(getCredits());
							if (getAnnimationThread() != null) {
								getAnnimationThread().terminate();
							}
							setAnnimationThread(new AnnimationThread());
							getAnnimationThread().start();
						} else {
							/**
							 * daca s-a selectat alt tab, oprim thread-ul de animare a
							 * credits-urilor. De ce? Pentru ca el TREBUIE sa afiseze stringul de la
							 * inceput de fiecare data cand se selecteaza.
							 */
							if (CreditsView.this.arrayCredits != null) {
								CreditsView.this.arrayCredits.clear();
								if (getAnnimationThread() != null) {
									getAnnimationThread().terminate();
								}
							}
							if ((CreditsView.this.styledTextCredits != null)
									&& !CreditsView.this.styledTextCredits.isDisposed()) {
								CreditsView.this.styledTextCredits.setText(CreditsView.ENCODE);
								StyleRange range1 = new StyleRange();
								range1.start = 0;
								range1.length = CreditsView.ENCODE.length();
								range1.underline = true;
								range1.font = CreditsView.FONT_CREDITS;
								CreditsView.this.styledTextCredits.setStyleRange(range1);
							}
						}
					}
				}
			});

			/**
			 * item APLICATIE
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_APLICATIE]).setControl(getTabItemContent(CreditsView.IDX_APLICATIE));
			/**
			 * item SYSTEM
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_SISTEM]).setControl(getTabItemContent(CreditsView.IDX_SISTEM));

			/**
			 * item SETARI
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_SETARI]).setControl(getTabItemContent(CreditsView.IDX_SETARI));
			/**
			 * item RESURSE
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_RESURSE]).setControl(getTabItemContent(CreditsView.IDX_RESURSE));
			/**
			 * item Versiuni
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_VERSIUNI]).setControl(getTabItemContent(CreditsView.IDX_VERSIUNI));
			/**
			 * item CREDITS
			 */
			addTabItem(getFolder(), CreditsView.tabNames[CreditsView.IDX_CREDITS]).setControl(getTabItemContent(CreditsView.IDX_CREDITS));

			getShell().pack();

		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	private Control getTabItemContent(final int idx) {
		switch (idx) {
			case IDX_APLICATIE: {
				return getTabAplicatieContent();
			}
			case IDX_CREDITS: {
				return getTabCreditsContent();
			}
			case IDX_RESURSE: {
				return getTabResurseContent();
			}
			case IDX_SETARI: {
				return getTabSettingsContent();
			}
			case IDX_VERSIUNI: {
				return getTabVersionsContent();
			}
			case IDX_SISTEM: {
				return getTabSystemContent();
			}
			default:
				throw new IllegalArgumentException("there's no such tab with IDX " + idx);
		}
	}

	private Composite getTabAplicatieContent() {
		setCompSplash(new Composite(getFolder(), SWT.BORDER));
		getCompSplash().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getCompSplash().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent ev) {
				if (((Composite) ev.widget).getBackgroundImage() != null) {
					((Composite) ev.widget).getBackgroundImage().dispose();
				}
			}
		});

		final Image logo = AppImages.getImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIGLA),
				this.sizeX,
				this.sizeY + 20);
		Image toBeDisposed = getCompSplash().getBackgroundImage();
		if (toBeDisposed != null) {
			if (!toBeDisposed.isDisposed()) {
				toBeDisposed.dispose();
			}
		}
		getCompSplash().setBackgroundImage(logo);
		final GC gc = new GC(getCompSplash().getBackgroundImage());
		Font font = new Font(Display.getDefault(), "Dialog", 12, SWT.BOLD | SWT.ITALIC);
		gc.setFont(font);
		gc.setTextAntialias(SWT.ON);
		gc.drawString("0.0.1", 200, 150, true);
		font.dispose();
		gc.dispose();

		getCompSplash().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						if ((getCompSplash() == null) || getCompSplash().isDisposed()) {
							return;
						}
						int x = 140, y = 50;
						Rectangle rect = getCompSplash().getClientArea();
						if (((rect.width - 3) <= 0) || (rect.height <= 0)) {
							return;
						}
						Rectangle rectBounds = getCompSplash().getBounds();
						if ((rectBounds.width - x) > 0) {
							x = rectBounds.width - x;
						} else {
							x = rectBounds.width + x;
						}
						if ((rectBounds.height - y) > 0) {
							y = rectBounds.height - y;
						} else {
							y = rectBounds.height + y;
						}
						final Image logoResize = AppImages.getImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_SIGLA),
								rect.width - 1,
								rect.height - 1);
						Image toBeDisposed2 = getCompSplash().getBackgroundImage();
						if (toBeDisposed2 != null) {
							if (!toBeDisposed2.isDisposed()) {
								toBeDisposed2.dispose();
							}
						}
						getCompSplash().setBackgroundImage(logoResize);
						final GC gcResize = new GC(getCompSplash().getBackgroundImage());
						Font fontResize = new Font(Display.getDefault(), "Dialog", 12, SWT.BOLD
								| SWT.ITALIC);
						gcResize.setFont(fontResize);
						gcResize.setTextAntialias(SWT.ON);
						gcResize.drawString("0.0.1", x, y, true);
						fontResize.dispose();
						gcResize.dispose();
					}
				});
			}
		});
		return getCompSplash();
	}

	private Composite getTabCreditsContent() {
		setCompCredits(new Composite(getFolder(), SWT.BORDER));
		getCompCredits().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getCompCredits().setLayout(new GridLayout(2, true));
		getCompCredits().setBackgroundMode(SWT.INHERIT_FORCE);
		getCompCredits().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent ev) {
				if (((Composite) ev.widget).getBackgroundImage() != null) {
					((Composite) ev.widget).getBackgroundImage().dispose();
				}
			}
		});

		final Image creditsImg = AppImages.getImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_LOGIN_OLD2),
				this.sizeX,
				this.sizeY + 20);
		Image toBeDisposed3 = getCompCredits().getBackgroundImage();
		if (toBeDisposed3 != null) {
			if (!toBeDisposed3.isDisposed()) {
				toBeDisposed3.dispose();
			}
		}
		getCompCredits().setBackgroundImage(creditsImg);

		getCompCredits().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						if ((getCompCredits() == null) || getCompCredits().isDisposed()) {
							return;
						}
						Rectangle rect = getCompCredits().getClientArea();
						if (((rect.width - 1) <= 0) || (rect.height <= 0)) {
							return;
						}
						final Image logoResize = AppImages.getImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_LOGIN_OLD2),
								rect.width - 1,
								rect.height);
						Image toBeDisposed4 = getCompCredits().getBackgroundImage();
						if (toBeDisposed4 != null) {
							if (!toBeDisposed4.isDisposed()) {
								toBeDisposed4.dispose();
							}
						}
						getCompCredits().setBackgroundImage(logoResize);
					}
				});
			}
		});
		getCompCredits().setBackgroundMode(SWT.INHERIT_FORCE);

		this.styledTextCredits = new StyledText(getCompCredits(), SWT.WRAP);
		GridData dataCanvas = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataCanvas.heightHint = 200;
		this.styledTextCredits.setLayoutData(dataCanvas);
		this.styledTextCredits.setAlignment(SWT.CENTER);
		this.styledTextCredits.setEnabled(false);
		this.styledTextCredits.setFont(FontUtil.TAHOMA10_BOLD);
		this.styledTextCredits.setLineSpacing(20);
		this.styledTextCredits.setText(CreditsView.ENCODE);
		StyleRange range = new StyleRange();
		range.start = 0;
		range.length = CreditsView.ENCODE.length();
		range.underline = true;
		range.font = CreditsView.FONT_CREDITS;
		this.styledTextCredits.setStyleRange(range);

		Label l = new Label(getCompCredits(), SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		l = new Label(getCompCredits(), SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		l = new Label(getCompCredits(), SWT.NONE);
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		return getCompCredits();
	}

	private Composite getTabResurseContent() {
		GridData gdata;

		setCompResurse(new Composite(getFolder(), SWT.BORDER));
		getCompResurse().setBackgroundMode(SWT.INHERIT_FORCE);
		getCompResurse().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getCompResurse().setLayout(new GridLayout(3, true));
		WidgetCompositeUtil.addXRenderListener(getCompResurse(),
				ColorUtil.COLOR_ALBASTRU_DESCHIS,
				ColorUtil.COLOR_FOCUS_YELLOW);

		final ProgressBar pbar = new ProgressBar(getCompResurse(), SWT.BORDER | SWT.SMOOTH);
		pbar.setForeground(ColorUtil.COLOR_FOCUS_YELLOW);
		gdata = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gdata.heightHint = 30;
		gdata.horizontalSpan = 3;
		pbar.setLayoutData(gdata);

		new Label(getCompResurse(), SWT.NONE);
		new Label(getCompResurse(), SWT.NONE).setText("[INFORMATII SISTEM]");
		new Label(getCompResurse(), SWT.NONE);

		final Label labelTotalMem = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelTotalMem.setLayoutData(gdata);

		final Label labelUsedMem = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelUsedMem.setLayoutData(gdata);

		final Label labelFreeMem = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelFreeMem.setLayoutData(gdata);

		final Label labelThreadCount = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelThreadCount.setLayoutData(gdata);
		labelThreadCount.setAlignment(SWT.LEFT);

		final Label labelDisplaySettings = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelDisplaySettings.setLayoutData(gdata);
		labelDisplaySettings.setText("Rezolutie curenta: "
				+ Display.getDefault().getPrimaryMonitor().getBounds().width + "x"
				+ Display.getDefault().getPrimaryMonitor().getBounds().height + " pixeli");

		final Label labelDisplayDepth = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(GridData.FILL_HORIZONTAL);
		gdata.horizontalSpan = 3;
		labelDisplayDepth.setLayoutData(gdata);
		labelDisplayDepth.setAlignment(SWT.LEFT);
		labelDisplayDepth.setText("Adancime culoare: " + Display.getDefault().getDepth() + " biti");

		final Label labelFont = new Label(getCompResurse(), SWT.NONE);
		gdata = new GridData(SWT.LEFT, SWT.NONE, true, false);
		gdata.horizontalSpan = 3;
		labelFont.setLayoutData(gdata);
		Font sysFont = Display.getDefault().getSystemFont();
		labelFont.setText("Font sistem: " + sysFont.getFontData()[0].getName() + ", dimensiune "
				+ sysFont.getFontData()[0].getHeight());

		final NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setGroupingUsed(true);
		/**
		 * apelurile pentru cantitatea de memorie facute in Runtime returneaza valori in bytes.
		 * Pentru a obtine Kb, impartim la 128.
		 */
		final double total = Runtime.getRuntime().totalMemory();
		double free = Runtime.getRuntime().freeMemory();
		double totalUsedBefore = total - free;
		pbar.setMaximum((int) total / (1024 * 1024));
		pbar.setSelection((int) totalUsedBefore / (1024 * 1024));
		labelUsedMem.setText("Memorie utilizata : " + nf.format(totalUsedBefore / (1024 * 1024))
				+ " MB");
		labelTotalMem.setText("Memorie alocata : " + nf.format(total / (1024 * 1024)) + " MB");

		/**
		 * Superb example of updating a widget from another thread
		 */
		new Thread() {

			@Override
			public void run() {
				do {
					final double totalM = Runtime.getRuntime().totalMemory();
					final double freeM = Runtime.getRuntime().freeMemory();
					final double totalUsedBeforeM = totalM - freeM;
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							if (!pbar.isDisposed()) {
								pbar.setMaximum((int) totalM / (1024 * 1024));
								pbar.setSelection((int) totalUsedBeforeM / (1024 * 1024));
							}
							if (!labelUsedMem.isDisposed()) {
								labelUsedMem.setText("Memorie utilizata : "
										+ nf.format(totalUsedBeforeM / (1024 * 1024)) + " MB");
							}
							if (!labelTotalMem.isDisposed()) {
								labelTotalMem.setText("Memorie alocata : "
										+ nf.format(totalM / (1024 * 1024)) + " MB");
							}
							if (!labelFreeMem.isDisposed()) {
								labelFreeMem.setText("Memorie libera : "
										+ nf.format(((int) Runtime.getRuntime().freeMemory())
												/ (1024 * 1024)) + " MB");
							}
							if (!labelThreadCount.isDisposed()) {
								labelThreadCount.setText("Fire de executie in paralel (threads) : "
										+ Thread.activeCount());
							}
						}
					});
					try {
						Thread.sleep(2000);
					}
					catch (InterruptedException exc) {
						logger.error(exc.getMessage(), exc);
					}
				}
				while (!getFolder().isDisposed());
			}
		}.start();
		return getCompResurse();
	}

	private Table getTabSettingsContent() {
		TableColumn col;

		setTableLocalSettings(new Table(getFolder(), SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER));
		col = new TableColumn(getTableLocalSettings(), SWT.NONE);
		col.setText("Proprietate");
		col.setWidth(150);
		col = new TableColumn(getTableLocalSettings(), SWT.NONE);
		col.setWidth(100);
		col.setText("Valoare");
		col.setWidth(150);
		getTableLocalSettings().setHeaderVisible(true);
		getTableLocalSettings().setLinesVisible(false);
		getTableLocalSettings().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		/**
		 * adaugam culoare custom, in gradient pe item-ul selectat
		 */
		WidgetTableUtil.addCustomGradientSelectionListenerToTable(getTableLocalSettings(),
				null,
				null);
		/**
		 * adaugam culoare custom, in gradient pe intreaga tabela
		 */
		WidgetCompositeUtil.addXRenderListener(getTableLocalSettings(),
				ColorUtil.COLOR_ALBASTRU_DESCHIS,
				ColorUtil.COLOR_FOCUS_YELLOW);

		TableItem item = new TableItem(getTableLocalSettings(), SWT.NONE);
		item.setText(0, "aaaa");
		item.setText(1, "bbbb");

		return getTableLocalSettings();
	}

	private Composite getTabVersionsContent() {
		Combo comboVersiuni;
		Composite comp;
		GridData gdata;

		comp = new Composite(getFolder(), SWT.NONE);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLay = new GridLayout(2, false);
		comp.setLayout(gridLay);

		new Label(comp, SWT.NONE).setText("Versiunea");

		comboVersiuni = new Combo(comp, SWT.READ_ONLY);
		gdata = new GridData(SWT.BEGINNING, SWT.NONE, false, false);
		gdata.widthHint = 100;
		comboVersiuni.setLayoutData(gdata);

		comboVersiuni.setItems(new String[]{"0.0.1"});
		comboVersiuni.addListener(SWT.Selection, new Listener() {
			@Override
			public final void handleEvent(final Event e) {
				Combo c = (Combo) e.widget;
				if (c.getSelectionIndex() == -1) {
					return;
				}
				final String versionInfo = "Text versiuni aplicatie";
				CreditsView.this.styledTextVersiuni.setText(versionInfo);

				int rangeLength = 0;
				if (versionInfo.indexOf(CreditsView.SEPARATOR) != -1) {
					rangeLength = versionInfo.substring(0,
							versionInfo.indexOf(CreditsView.SEPARATOR)).length();
				}
				if (rangeLength > 0) {
					StyleRange range = new StyleRange();
					range.start = 0;
					range.length = rangeLength;
					range.underline = true;
					CreditsView.this.styledTextVersiuni.setStyleRange(range);
				}
				CreditsView.this.compVersions.setMinSize(CreditsView.this.styledTextVersiuni.computeSize(SWT.DEFAULT,
						SWT.DEFAULT));
			}
		});

		this.compVersions = new ScrolledComposite(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdata.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
		this.compVersions.setLayoutData(gdata);
		this.compVersions.setExpandHorizontal(true);
		this.compVersions.setExpandVertical(true);
		this.compVersions.setLayout(new GridLayout(1, false));
		/**
		 * setand focus-ul pe scrolled comp, va functiona scroll-ul componentelor desenate pe el,
		 * folosing rotitza din mijloc a mouse-ului.
		 */
		this.compVersions.addListener(SWT.Activate, new Listener() {
			@Override
			public final void handleEvent(final Event event) {
				((ScrolledComposite) event.widget).setFocus();
			}

		});
		/**
		 * cum viteza scroll-ului in cazul asta este foarte mica, o marim de 5 ori.
		 */
		this.compVersions.getVerticalBar().setIncrement(this.compVersions.getVerticalBar().getIncrement() * 5);

		this.styledTextVersiuni = new StyledText(this.compVersions, SWT.WRAP);
		this.styledTextVersiuni.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.styledTextVersiuni.setAlignment(SWT.LEFT);
		this.styledTextVersiuni.setEnabled(false);
		this.styledTextVersiuni.setFont(FontUtil.TAHOMA10_BOLD);
		this.styledTextVersiuni.setBackground(ColorUtil.COLOR_FOCUS_YELLOW);

//		if (comboVersiuni.indexOf(Version.getRuntimeVersion()) != -1) {
//			comboVersiuni.select(comboVersiuni.indexOf(Version.getRuntimeVersion()));
//			comboVersiuni.notifyListeners(SWT.Selection, new Event());
//		}

		this.compVersions.setContent(this.styledTextVersiuni);

		return comp;
	}

	private Table getTabSystemContent() {
		setTableSysProps(new Table(getFolder(), SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER
				| SWT.V_SCROLL));
		TableColumn col = new TableColumn(getTableSysProps(), SWT.NONE);
		col.setText("Proprietate");
		col.setWidth(150);
		col = new TableColumn(getTableSysProps(), SWT.NONE);
		col.setWidth(100);
		col.setText("Valoare");
		col.setWidth(150);
		getTableSysProps().setHeaderVisible(true);
		getTableSysProps().setLinesVisible(false);
		getTableSysProps().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		WidgetTableUtil.addCustomGradientSelectionListenerToTable(getTableSysProps(), null, null);
		WidgetCompositeUtil.addXRenderListener(getTableSysProps(),
				ColorUtil.COLOR_ALBASTRU_DESCHIS,
				ColorUtil.COLOR_FOCUS_YELLOW);

		Properties prop = System.getProperties();
		ArrayList<String> sortedPropNames = new ArrayList<String>();
		for (Enumeration<?> en = prop.propertyNames(); en.hasMoreElements();) {
			String str = en.nextElement().toString();
			if (str.startsWith("com.encode.borg.")) {
				continue;
			}
			sortedPropNames.add(str);
		}
		Collections.sort(sortedPropNames, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < sortedPropNames.size(); i++) {
			String str = sortedPropNames.get(i);
			TableItem item = new TableItem(getTableSysProps(), SWT.NONE);
			item.setText(0, str);
			item.setText(1, prop.get(str) != null ? prop.get(str).toString() : "");
		}
		return getTableSysProps();
	}

	private void setCompSplash(final Composite compSplash) {
		this.compSplash = compSplash;
	}

	private Composite getCompSplash() {
		return this.compSplash;
	}

	private Composite getCompCredits() {
		return this.compCredits;
	}

	private void setCompCredits(final Composite compCredits) {
		this.compCredits = compCredits;
	}

	private class AnnimationThread extends Thread {

		private volatile boolean isTerminated = false;

		/**
		 * For various reasons beginning with CPU and ending with OS, there is NO GUARANTEE that
		 * Thread actually sleeps the indicated amount. I've noticed on X64 some undersleep
		 * behaviours that are very annoying for this Thread purpose, to render a text and wait for
		 * the text to be read. This method compensate for Thread's fault.
		 * 
		 * @param millis
		 * @throws InterruptedException
		 */
		public void sleepAtLeast(final long millis) throws InterruptedException {
			long t0 = System.currentTimeMillis();
			long millisLeft = millis;
			while (millisLeft > 0) {
				Thread.sleep(millisLeft);
				long t1 = System.currentTimeMillis();
				millisLeft = millis - (t1 - t0);
			}
		}

		public void terminate() {
			this.isTerminated = true;
		}

		@Override
		public void run() {
			/**
			 * Exista un bug si anume, daca faci fast switch intre tab-uri, thread-ul vechi nu
			 * devine null si vom avea 2 thread-uri care afiseaza pe aceeasi suprafata, si procedeul
			 * continua. Probabil se datoreaza faptului ca se revine de pe un tab pe tab-ul credits
			 * intr-un interval mai scurt de 3 secunde si thread-ul anterior este inca adormit, el
			 * nu devine null, iar in momentul trezirii nu mai este valabila conditia de terminare a
			 * sa. Solutionare: s-a implementat metoda terminate(), este echivalenta metodei
			 * devenita deprecated Thread.stop();
			 */
			final StringBuffer buff = new StringBuffer();
			try {
				while ((CreditsView.this.arrayCredits != null)
						&& (CreditsView.this.arrayCredits.size() > 0) && !this.isTerminated) {
					try {
						this.sleepAtLeast(3000);
					}
					catch (InterruptedException exc) {
						logger.error(exc.getMessage(), exc);
					}
					try {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								if ((getFolder() == null)
										|| getFolder().isDisposed()
										|| (getFolder().getSelectionIndex() == -1)
										|| !getFolder().getSelection().getText().equalsIgnoreCase(CreditsView.tabNames[CreditsView.IDX_CREDITS])
										|| (CreditsView.this.styledTextCredits == null)
										|| CreditsView.this.styledTextCredits.isDisposed()) {
									CreditsView.this.arrayCredits.clear();
									return;
								}
								buff.setLength(0);
								buff.append(CreditsView.ENCODE);
								buff.append(CreditsView.SEPARATOR);
								if (CreditsView.this.arrayCredits.size() > 0) {
									buff.append(CreditsView.this.arrayCredits.get(0));
								}
								buff.append(CreditsView.SEPARATOR);
								if (CreditsView.this.arrayCredits.size() > 0) {
									CreditsView.this.arrayCredits.remove(0);
								}
								if (CreditsView.this.arrayCredits.size() > 0) {
									buff.append(CreditsView.this.arrayCredits.get(0));
									buff.append(CreditsView.SEPARATOR);
									CreditsView.this.arrayCredits.remove(0);
								}
								CreditsView.this.styledTextCredits.setText(buff.toString());
								StyleRange range = new StyleRange();
								range.start = 0;
								range.length = CreditsView.ENCODE.length();
								range.underline = true;
								range.font = CreditsView.FONT_CREDITS;
								CreditsView.this.styledTextCredits.setStyleRange(range);
								if (CreditsView.this.arrayCredits.size() == 0) {
									CreditsView.this.arrayCredits.addAll(getCredits());
								}
							}
						});
					}
					catch (Exception exc) {
						logger.error(exc.getMessage(), exc);
						return;
					}
				}
			}
			catch (Exception exc) {
				logger.error(exc.getMessage(), exc);
			}
		}
	}

	private List<String> getCredits() {
		List<String> array = new ArrayList<String>();
		try {
			final StringTokenizer tokens = new StringTokenizer(CreditsView.credits, "\r\n\t", false);
			while (tokens.hasMoreTokens()) {
				array.add(tokens.nextToken());
			}
		}
		catch (Exception exc) {
			array.clear();
			logger.error(exc.getMessage(), exc);
		}
		return array;
	}

	private void setFolder(final CTabFolder folder) {
		this.folder = folder;
	}

	private CTabFolder getFolder() {
		return this.folder;
	}

	private AnnimationThread getAnnimationThread() {
		return this.annimationThread;
	}

	private void setAnnimationThread(final AnnimationThread th) {
		this.annimationThread = th;
	}

	private CTabItem addTabItem(final CTabFolder parent, final String TAB_ITEM_NAME) {
		CTabItem item = null;
		if ((parent == null) || parent.isDisposed()) {
			return null;
		}
		item = new CTabItem(getFolder(), SWT.NONE);
		item.setImage(AppImages.getImageMiscByName(AppImages.IMG_MISC_HYPERCUBE_ICO));
		item.setText(TAB_ITEM_NAME);
		return item;
	}

	private void setCompResurse(final Composite compResurse) {
		this.compResurse = compResurse;
	}

	private Composite getCompResurse() {
		return this.compResurse;
	}

	private void setTableSysProps(final Table tableSysProps) {
		this.tableSysProps = tableSysProps;
	}

	private Table getTableSysProps() {
		return this.tableSysProps;
	}

	private void setTableLocalSettings(final Table tableLocalSettings) {
		this.tableLocalSettings = tableLocalSettings;
	}

	private Table getTableLocalSettings() {
		return this.tableLocalSettings;
	}

	@Override
	public void run() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public final void run() {
				addComponents();
				open(false, false);
			}
		});
	}

}
