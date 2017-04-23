package com.papao.books.view.auth;

import org.apache.log4j.Logger;

import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * This class creates a splash panel to the size of the image to be displayed. The panel is displayed for as long as is required to load required classes, create connection to database, load config
 * and necessary data.
 */
public class SplashPanel extends Canvas {

	private static final long serialVersionUID = 3457535893727558553L;

	/** This object's font metrics */
	private final FontMetrics fontMetrics;

	/** The window displayed */
	private final Window window;

	/** The splash image */
	private Image image;

	/** The off-screen image */
	private Image offscreenImg;

	/** The off-screen graphics */
	private Graphics offscreenGfx;

	/** The startup progress positon */
	private int progress;

	/** The version info string */
	private String version;

	/** The progress bar's colour */
	private final Color progressColour;

	/** the light gradient colour */
	private final Color gradientColour;

	/** the x-coord of the version string */
	private int horizontalCoord;

	/** the y-coord of the version string */
	private int verticalCoord;

	/** The progress bar height */
	private final static int PROGRESS_HEIGHT = 20;

	private static Logger logger = Logger.getLogger(SplashPanel.class);

	/** Creates a new instance of the splash panel. */
	public SplashPanel(final Color progressBarColour, final String imageResourcePath, final String versionNumber) {
		this(progressBarColour, imageResourcePath, versionNumber, -1, -1);
	}

	/**
	 * @param progressBarColour
	 *            the colour of progress bar
	 * @param imageResourcePath
	 *            canonical name of the image to be rendered, eg. "/com/encode/borg/ui/resources/images/CompanyLogo2.png"
	 * @param versionNumber
	 *            this number will be rendered ON TOP of the displayed image, with prefix "Version"
	 * @param horizontalCoord
	 *            horizontal coordinate to render versionNumber
	 * @param verticalCoord
	 *            vertical coordinate to render versionNumber
	 */
	public SplashPanel(final Color progressBarColour, final String imageResourcePath, final String versionNumber, final int horizontalCoord, final int verticalCoord) {
		super();
		this.horizontalCoord = horizontalCoord;
		this.verticalCoord = verticalCoord;

		this.progressColour = progressBarColour;
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		setBackground(Color.white);

		this.gradientColour = SplashPanel.getBrighter(progressBarColour, 0.25);

		final Font font = new Font("Dialog", Font.PLAIN, 15);
		setFont(font);
		this.fontMetrics = getFontMetrics(font);

		try {
			final URL imageURL = getClass().getResource(imageResourcePath);
			if (imageURL == null) {
				throw new FileNotFoundException("image " + imageResourcePath + " not found!");
			}
			this.image = getToolkit().getImage(imageURL);
		}
		catch (Exception exc) {
			logger.fatal(exc, exc);
		}

		final MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(this.image, 0);

		if (versionNumber != null) {
			this.version = "Version " + versionNumber;
		}

		try {
			tracker.waitForAll();
		}
		catch (Exception e) {
			logger.fatal(e, e);
		}

		this.window = new Window(new Frame());

		final Dimension screen = getToolkit().getScreenSize();
		final Dimension size = new Dimension(this.image.getWidth(this), this.image.getHeight(this));
		this.window.setSize(size);

		this.window.setLayout(new BorderLayout());
		this.window.add(BorderLayout.CENTER, this);

		this.window.setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);
		this.window.validate();
		this.window.setVisible(true);
		this.window.toFront();

	}

	public void advance() {
		synchronized (this) {
			this.progress++;
			repaint();
		}
	}

	/**
	 * @param g
	 *            java.awt.Graphics
	 */
	@Override
	public void paint(final Graphics g) {
		synchronized (this) {

			final Dimension size = getSize();

			if (this.offscreenImg == null) {
				this.offscreenImg = createImage(size.width, size.height);
				this.offscreenGfx = this.offscreenImg.getGraphics();
				final Font font = new Font("dialog", Font.PLAIN, 12);
				this.offscreenGfx.setFont(font);
			}

			this.offscreenGfx.drawImage(this.image, 0, 0, this);

			this.offscreenGfx.setColor(this.progressColour);

			((Graphics2D) this.offscreenGfx).setPaint(new GradientPaint(0, this.image.getHeight(this) - SplashPanel.PROGRESS_HEIGHT, this.gradientColour,
			// new Color(95,95,190),
				0,
				this.image.getHeight(this),
				this.progressColour));

			this.offscreenGfx.fillRect(0, this.image.getHeight(this) - SplashPanel.PROGRESS_HEIGHT, (this.progress * (this.window.getWidth() / 10)), SplashPanel.PROGRESS_HEIGHT);

			if (this.version != null) {

				if (this.horizontalCoord == -1) {
					this.horizontalCoord = (getWidth() - this.fontMetrics.stringWidth(this.version)) / 2;
				}

				if (this.verticalCoord == -1) {
					// if no y value - set just above progress bar
					this.verticalCoord = this.image.getHeight(this) - SplashPanel.PROGRESS_HEIGHT - this.fontMetrics.getHeight();
				}

				((Graphics2D) this.offscreenGfx).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D) this.offscreenGfx).setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				((Graphics2D) this.offscreenGfx).setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				((Graphics2D) this.offscreenGfx).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

				this.offscreenGfx.setColor(Color.BLACK);
				this.offscreenGfx.drawString(this.version, this.horizontalCoord, this.verticalCoord);
			}

			g.drawImage(this.offscreenImg, 0, 0, this);

		}
	}

	public static Color getBrighter(final Color color, final double factor) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		final int i = (int) (1.0 / (1.0 - factor));
		if ((r == 0) && (g == 0) && (b == 0)) {
			return new Color(i, i, i);
		}
		if ((r > 0) && (r < i)) {
			r = i;
		}
		if ((g > 0) && (g < i)) {
			g = i;
		}
		if ((b > 0) && (b < i)) {
			b = i;
		}

		return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255));
	}

	public void dispose() {
		// wait a moment
		try {
			Thread.sleep(1000);
		}
		catch (Exception exc) {
			logger.fatal(exc, exc);
		}
		this.window.dispose();
	}

	/**
	 * @param g
	 *            java.awt.Graphics
	 */
	@Override
	public void update(final Graphics g) {
		paint(g);
	}

	public void reset() {
		synchronized (this) {
			this.progress = 0;
		}
	}

}
