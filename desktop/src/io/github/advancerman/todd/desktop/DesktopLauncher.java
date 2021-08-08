package io.github.advancerman.todd.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import io.github.advancerman.todd.launcher.ToddGame;

import static io.github.advancerman.todd.util.SettingsKt.SCREEN_HEIGHT;
import static io.github.advancerman.todd.util.SettingsKt.SCREEN_WIDTH;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = SCREEN_WIDTH;
		config.height = SCREEN_HEIGHT;
		new LwjglApplication(new ToddGame(), config);
	}
}
