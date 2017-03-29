package com.papao.books.ui.providers;

import com.papao.books.auth.EncodeLive;
import com.papao.books.ui.custom.ComboImage;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public final class ContentProposalProvider implements IContentProposalProvider {

	private final String[] data;
	private final boolean isCursorAtStart;
	private static Logger logger = Logger.getLogger(ContentProposalProvider.class);

	private ContentProposalProvider(final String[] data, final boolean isCursorAtStart) {
		if (data != null) {
			this.data = data.clone();
		} else {
			this.data = new String[0];
		}
		this.isCursorAtStart = isCursorAtStart;
	}

	@Override
	public IContentProposal[] getProposals(final String contents, final int position) {
		List<IContentProposal> cpList = new ArrayList<IContentProposal>();
		for (int i = 0; i < this.data.length; ++i) {
			final String candidate = this.data[i];
			if (isContentProposal(candidate, contents)) {
				cpList.add(new IContentProposal() {

					@Override
					public String getLabel() {
						return candidate;
					}

					@Override
					public String getDescription() {
						return null;
					}

					@Override
					public int getCursorPosition() {
						return ContentProposalProvider.this.isCursorAtStart	? 0
																			: candidate.length();
					}

					@Override
					public String getContent() {
						return candidate;
					}
				});
			}
		}

		IContentProposal[] cpArray = new IContentProposal[cpList.size()];

		return cpList.toArray(cpArray);
	}

	private boolean isContentProposal(final String candidate, final String content) {
		if (StringUtils.isEmpty(candidate) || StringUtils.isEmpty(content)) {
			return false;
		}
		return candidate.toLowerCase(EncodeLive.ROMANIAN_LOCALE).contains(content.toLowerCase(EncodeLive.ROMANIAN_LOCALE));
	}

	public static char[] getAlphaNumericChars() {
		String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		String deleteChar = new String(new char[] {
			8 });

		String combo = alphanum + deleteChar;

		return combo.toCharArray();
	}

	public static void addContentProposal(	final Control control,
											final String[] proposals,
											final boolean isCursorAtZeroIndex) {
		try {
			if ((control == null) || control.isDisposed() || (proposals == null)
					|| (proposals.length == 0)) {
				return;
			}
			ContentProposalAdapter cpAdapter;
			// char[] autoActivationCharacters = new char[] {
			// '.', '#' };
			if (control instanceof Text) {
				cpAdapter = new ContentProposalAdapter(
					control,
					new TextContentAdapter(),
					new ContentProposalProvider(proposals, isCursorAtZeroIndex),
					// KeyStroke.getInstance("Ctrl+Space"),
					null,
					ContentProposalProvider.getAlphaNumericChars());
			} else if (control instanceof Combo) {
				cpAdapter = new ContentProposalAdapter(
					control,
					new ComboContentAdapter(),
					new ContentProposalProvider(proposals, isCursorAtZeroIndex),
					// KeyStroke.getInstance("Ctrl+Space"),
					null,
					ContentProposalProvider.getAlphaNumericChars());
				final Combo combo = (Combo) control;
				if (combo.getParent() instanceof ComboImage) {
					combo.addListener(SWT.KeyUp, new Listener() {
						@Override
						public final void handleEvent(final Event e) {
							/**
							 * Asta ar fi event-ul de selectie in combo, si s-ar notifica de 2 ori
							 * listenerii pe SWT.Selection !! Asta poate avea rezultate dezastruoase
							 * cand una din actiunile pe selectia in combo e un select in db!! Am
							 * inclus si verificarile pentru tastele : Alt, Shift, Ctrl ca sa nu
							 * generez un event de selectie la apasarea lor, in cazul in care in
							 * combo exista o valoare ok, dar se da de ex un Alt+Tab.
							 **/
							if ((e.keyCode == SWT.ARROW_UP) || (e.keyCode == SWT.ARROW_DOWN)
									|| (e.keyCode == SWT.ALT) || (e.keyCode == SWT.SHIFT)
									|| (e.keyCode == SWT.CTRL) || (e.keyCode == SWT.END)
									|| (e.keyCode == SWT.HOME)) {
								return;
							}
							e.doit = true;
							if (combo.indexOf(combo.getText()) != -1) {
								final ComboImage ci = (ComboImage) combo.getParent();
								ci.select(combo.getText());
							}
						}
					});
					combo.addListener(SWT.MouseDown, new Listener() {
						@Override
						public final void handleEvent(final Event e) {
							if (combo.getSelectionIndex() != -1) {
								return;
							}
							e.doit = true;
							if (combo.indexOf(combo.getText()) != -1) {
								if (combo.getParent() instanceof ComboImage) {
									final ComboImage ci = (ComboImage) combo.getParent();
									ci.select(combo.getText());
								} else {
									combo.select(combo.indexOf(combo.getText()));
								}
							}
						}
					});
				} else {
					combo.addListener(SWT.Modify, new Listener() {
						@Override
						public final void handleEvent(final Event e) {
							final int index = combo.indexOf(combo.getText());
							if (index != -1) {
								combo.select(index);
								combo.notifyListeners(SWT.Selection, new Event());
							}
						}
					});
				}
			} else {
				logger.warn(ContentProposalProvider.class.getCanonicalName()
						+ "#addContentProposal() was called with an invalid argument [" + control
						+ "]. Content proposal currently supports only Text and Combo widgets!");
				return;
			}
			cpAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		}
		catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
		}
	}

}
