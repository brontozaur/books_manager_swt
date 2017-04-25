package com.notification;

import java.util.EventListener;

public interface NotificationListener extends EventListener {
	void onEvent(NotificationEvent event);
}
