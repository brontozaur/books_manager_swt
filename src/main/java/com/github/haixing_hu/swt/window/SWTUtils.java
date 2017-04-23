/******************************************************************************
 *
 * Copyright (c) 2014  Haixing Hu
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Haixing Hu (https://github.com/Haixing-Hu/) - Initial implementation and API.
 *
 ******************************************************************************/

package com.github.haixing_hu.swt.window;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Haixing Hu
 */
public final class SWTUtils {

  private static Logger LOGGER = LoggerFactory.getLogger(SWTUtils.class);

  private SWTUtils() {
    // empty
  }

  /**
   * Center a shell on the primary monitor
   *
   * @param shell
   *          shell to center
   */
  public static void centerShell(final Shell shell) {
    final Monitor primary = shell.getDisplay().getPrimaryMonitor();
    final Rectangle bounds = primary.getBounds();
    final Rectangle rect = shell.getBounds();
    final int x = bounds.x + ((bounds.width - rect.width) / 2);
    final int y = bounds.y + ((bounds.height - rect.height) / 2);
    shell.setLocation(x, y);
  }

  /**
   * Gets the bounds of the monitor on which the specified shell is running.
   *
   * @param shell
   *          a specified shell.
   * @return the bounds of the monitor on which the shell is running.
   */
  public static Rectangle getMonitorBounds(final Shell shell) {
    final Display display = shell.getDisplay();
    for (final Monitor monitor : display.getMonitors()) {
      final Rectangle monitorBounds = monitor.getBounds();
      final Rectangle shellBounds = shell.getBounds();
      if (monitorBounds.contains(shellBounds.x, shellBounds.y)) {
        return monitorBounds;
      }
    }
    final Monitor primary = display.getPrimaryMonitor();
    return primary.getBounds();
  }
}
