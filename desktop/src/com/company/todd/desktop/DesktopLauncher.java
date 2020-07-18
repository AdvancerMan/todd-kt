package com.company.todd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.company.todd.launcher.ToddGame;

import static com.company.todd.util.SettingsKt.SCREEN_HEIGHT;
import static com.company.todd.util.SettingsKt.SCREEN_WIDTH;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = SCREEN_WIDTH;
		config.height = SCREEN_HEIGHT;
		new LwjglApplication(new ToddGame(), config);
	}
}
