package com.papao.books.view.util;

import com.papao.books.view.custom.XButton;
import com.papao.books.view.view.SWTeXtension;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public final class WidgetCompositeUtil {

	private static Logger logger = Logger.getLogger(WidgetComboUtil.class);

	private WidgetCompositeUtil() {}

	public static void centerInDisplay(final Composite parent) {
		try {
			if ((parent == null) || parent.isDisposed()) {
				return;
			}
			Point parentBounds = parent.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			parent.setLocation((Display.getDefault().getPrimaryMonitor().getBounds().width - parentBounds.x) / 2,
					(Display.getDefault().getPrimaryMonitor().getBounds().height - parentBounds.y) / 2);
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

    public static void centerInDisplayForFixedWidthsShells(final Composite parent) {
        try {
            if ((parent == null) || parent.isDisposed()) {
                return;
            }
            parent.setLocation((Display.getDefault().getPrimaryMonitor().getBounds().width - parent.getBounds().width) / 2,
                    (Display.getDefault().getPrimaryMonitor().getBounds().height - parent.getBounds().height) / 2);
        }
        catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

	/**
	 * @param parent
	 *            suportul pe care se vor cauta in mod recursiv componentele Daca acesta nu are setata o imagine, ca sa obtinem ImageData din aceasta, se va crea o noua imagine, in gradient, folosind
	 *            c1 si c2, iar toti descendentii suportului parent vor mosteni aceasta imagine, indiferent de nivel
	 * @param c1
	 *            culoare nr 1 - afisata in partea stanga
	 * @param c2
	 *            culoare nr 2 - afisata in partea dreapta Atentie! Daca avem 2 shell-uri, shell1 si shell2, iar shell2 = new Shell(shell1, style), din motive necunoscute, shell1.getChildren() are
	 *            lungimea 0! cu toate ca ar tb sa fie 1.
	 */
	public static void addGradient2Childrens(final Composite parent, final Color c1, final Color c2) {
		try {
			if ((parent == null) || parent.isDisposed()) {
				return;
			}
			if ((c1 == null) || c1.isDisposed()) {
				return;
			}
			if ((c2 == null) || c2.isDisposed()) {
				return;
			}
			if (parent instanceof Shell) {
				if ((((Shell) parent).getBackgroundImage() == null) || ((Shell) parent).getBackgroundImage().isDisposed()) {
					SWTeXtension.addGradientListener((Shell) parent, c1, c2);
				}
			} else {
				if ((((Control) parent).getBackgroundImage() == null) || ((Control) parent).getBackgroundImage().isDisposed()) {
					SWTeXtension.addGradientListener2Widget(parent, c1, c2);
				}
            }
			Control[] ctr = parent.getChildren();
			if ((ctr == null) || (ctr.length == 0)) {
				return;
			}
			for (int i = 0; i < ctr.length; i++) {
				if ((ctr[i] != null) && !ctr[i].isDisposed()) {
					SWTeXtension.addGradientListener2Widget(ctr[i], c1, c2);
					if (ctr[i] instanceof Composite) {
						WidgetCompositeUtil.addGradient2Childrens((Composite) ctr[i], c1, c2);
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static void addColoredFocusListener2Childrens(final Composite parent) {
		WidgetCompositeUtil.addColoredFocusListener2Childrens(parent, null);
	}

	public static void addColoredFocusListener2Childrens(final Composite parent, final Color c1) {
		Color c2 = null;
		try {
			if ((parent == null) || parent.isDisposed()) {
				return;
			}
			if ((c1 == null) || c1.isDisposed()) {
				c2 = ColorUtil.COLOR_FOCUS_YELLOW;
			}
			Control[] ctr = parent.getChildren();
			if ((ctr == null) || (ctr.length == 0)) {
				return;
			}
			for (int i = 0; i < ctr.length; i++) {
				if ((ctr[i] != null) && !ctr[i].isDisposed()) {
					if (!(ctr[i] instanceof Link) && !(ctr[i] instanceof StyledText) && !(ctr[i] instanceof ToolBar) && !(ctr[i] instanceof CTabFolder)
							&& !(ctr[i] instanceof XButton)) {
						SWTeXtension.addColoredFocusListener(ctr[i], c1 != null ? c1 : c2);
					}
					if (ctr[i] instanceof Composite) {
						WidgetCompositeUtil.addColoredFocusListener2Childrens((Composite) ctr[i], c1 != null ? c1 : c2);
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static void renderXEfect(final Composite parent, final Color c1, final Color c2) {
		try {
			if ((parent == null) || parent.isDisposed()) {
				return;
			}
			if ((c1 == null) || c1.isDisposed()) {
				return;
			}
			if ((c2 == null) || c2.isDisposed()) {
				return;
			}
			if (parent instanceof Shell) {
				SWTeXtension.addGradientListener((Shell) parent, c1, c2);
			}
			WidgetCompositeUtil.addXRenderListener(parent, c1, c2);
			Control[] ctr = parent.getChildren();
			if ((ctr == null) || (ctr.length == 0)) {
				return;
			}
			for (int i = 0; i < ctr.length; i++) {
				if ((ctr[i] instanceof Shell) && (((Shell) ctr[i]).getChildren().length > 0)) {
					WidgetCompositeUtil.renderXEfect((Shell) ctr[i], c1, c2);
				}

			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static void addXRenderListener(final Composite parent, final Color c1, final Color c2) {
		Color c1Param = c1;
		Color c2Param = c2;
		try {
			if ((parent == null) || parent.isDisposed()) {
				return;
			}
			if ((c1Param == null) || c1Param.isDisposed()) {
				c1Param = ColorUtil.COLOR_ROSU_SEMI_ROSU2;
			}
			if ((c2Param == null) || c2Param.isDisposed()) {
				c2Param = ColorUtil.COLOR_FOCUS_YELLOW;
			}
			final Color c11 = c1Param;
			final Color c12 = c2Param;
			parent.setBackgroundMode(SWT.INHERIT_FORCE);
			parent.addListener(SWT.Resize, new Listener() {

				@Override
				public void handleEvent(final Event e) {
					Display display = parent.getDisplay();
					Rectangle rect = parent.getClientArea();
					if ((rect.width <= 0) || (rect.height <= 0)) {
						return;
					}
					Image imageGradient = new Image(display, rect.width, rect.height);
					GC gc = new GC(imageGradient);
					try {
						gc.setForeground(c11);
						gc.setBackground(c12);
						gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
					}
					finally {
						gc.dispose();
					}
					if ((parent.getBackgroundImage() != null) && !parent.getBackgroundImage().isDisposed()) {
						parent.getBackgroundImage().dispose();
					}
					parent.setBackgroundImage(imageGradient);

				}
			});

		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	/**
	 * enable sau disable componente de pe un composite/group, recursiv
	 * 
	 * @param comp
	 * @param flag
	 */
	public static void enableGUI(final Composite comp, final boolean flag) {
		try {
			if ((comp == null) || comp.isDisposed()) {
				return;
			}
			Control[] childrens = comp.getChildren();
			if (childrens != null) {
				for (int i = 0; i < childrens.length; i++) {
					if (!childrens[i].isDisposed()) {
						if (childrens[i] instanceof Table) {
							Table tab = (Table) childrens[i];
							TableItem[] items = tab.getItems();
							if (items.length > 0) {
								for (int k = 0; k < items.length; k++) {
									if (!flag) {
										items[k].setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
									} else {
										items[k].setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
									}
								}
							}
							if (!flag) {
								tab.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
							}
						} else if (!(childrens[i] instanceof Label)) {
							if (childrens[i] instanceof Text) {
								Text text = (Text) childrens[i];
								if ((text.getStyle() & SWT.SEARCH) != SWT.SEARCH) {
									text.setMessage("");

								}
								childrens[i].setEnabled(flag);
							} else {
								childrens[i].setEnabled(flag);
							}
							if ((childrens[i] instanceof Text) || (childrens[i] instanceof Combo)) {
								if (!flag) {
									childrens[i].setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
								} else {
									childrens[i].setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
								}
							}
						}
					}
					if (childrens[i].isDisposed()) {
						continue;
					}
					Control[] ch = null;
					if (childrens[i] instanceof Composite) {
						ch = ((Composite) childrens[i]).getChildren();
						if ((ch != null) && (ch.length > 0)) {
							WidgetCompositeUtil.enableGUI((Composite) childrens[i], flag);
						}
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	/**
	 * sterge text-ul afisat, deselecteaza elementele din tabela, selectia pe SWT.CHECK, selectia in combo, etc
	 * 
	 * @param comp
	 */
	public static void clearFields(final Composite comp) {
		try {
			if ((comp == null) || comp.isDisposed()) {
				return;
			}
			Control[] childrens = comp.getChildren();
			if (childrens != null) {
				for (int i = 0; i < childrens.length; i++) {
					if (!childrens[i].isDisposed()) {
						if (childrens[i] instanceof Text) {
							((Text) childrens[i]).setText("");
						} else if (childrens[i] instanceof Button) {
							Button b = (Button) childrens[i];
							if (((b.getStyle() & SWT.CHECK) == SWT.CHECK) || ((b.getStyle() & SWT.RADIO) == SWT.RADIO) || ((b.getStyle() & SWT.TOGGLE) == SWT.TOGGLE)) {
								b.setSelection(false);
							}
						} else if (childrens[i] instanceof Table) {
							((Table) childrens[i]).deselectAll();
						} else if (childrens[i] instanceof Combo) {
							((Combo) childrens[i]).deselectAll();
						}
					}
					if (childrens[i].isDisposed()) {
						continue;
					}
					Control[] ch = null;
					if (childrens[i] instanceof Composite) {
						ch = ((Composite) childrens[i]).getChildren();
						if ((ch != null) && (ch.length > 0)) {
							WidgetCompositeUtil.clearFields((Composite) childrens[i]);
						}
					}
				}
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

	public static int[] getMiddleScreenCoords(final Composite parent) {
		try {
			if ((parent == null) || parent.isDisposed()) {
				return new int[] {
						200, 200 };
			}
			return new int[] {
					(Display.getDefault().getBounds().width - parent.getBounds().width) / 2, (Display.getDefault().getBounds().height - parent.getBounds().height) / 2 };
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
			return new int[] {
					200, 200 };
		}
	}

	/**
	 * pozitioneaza fereastra in centru ferestrei parinte
	 * 
	 * @param parentSize
	 * @param childSize
	 * @return
	 */
	public static Point centerInWindow(final Rectangle parentSize, final Rectangle childSize) {
		Point childLocation = new Point(0, 0);
		try {
			childLocation.x = parentSize.x + ((parentSize.width - childSize.width)) / 2;
			if (childLocation.x < 0) {
				childLocation.x = 0;
			} else if (childLocation.x + childSize.width > Display.getDefault().getBounds().width) {
				childLocation.x = Display.getDefault().getBounds().width - childSize.width;
			}
			childLocation.y = parentSize.y + ((parentSize.height - childSize.height)) / 2;
			if (childLocation.y < 0) {
				childLocation.y = 0;
			} else if (childLocation.y + childSize.height > Display.getDefault().getBounds().height) {
				childLocation.y = Display.getDefault().getBounds().height - childSize.height;
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
		return childLocation;
	}

	public static void enableControls(final Control[] controls, final Control upperEnabledParent) {
		try {
			if ((controls == null) || (controls.length == 0) || (upperEnabledParent == null) || upperEnabledParent.isDisposed()) {
				return;
			}
			for (Control control : controls) {
				if ((control == null) || control.isDisposed()) {
					continue;
				}
				control.setEnabled(true);
				control.setBackground(null);
				if (control instanceof Composite) {
					WidgetCompositeUtil.enableGUI((Composite) control, true);
				}
				if (control instanceof Text) {
					((Text) control).setEditable(true);
				}
				Control parent = control.getParent();
				do {
					parent.setEnabled(true);
					parent = parent.getParent();
				}
				while ((parent != null) && (parent != upperEnabledParent));
			}
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

}
