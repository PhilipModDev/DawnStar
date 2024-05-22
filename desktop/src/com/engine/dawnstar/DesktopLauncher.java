package com.engine.dawnstar;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.engine.dawnstar.client.DawnStarClient;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {

		System.out.println(Runtime.version().toString());
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(120);
		config.useVsync(false);
		config.setTitle("DawnStarClient");
		config.setWindowIcon("dawnstar.png");
		//Set the openGL version to gl32.
		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2);
		config.setWindowedMode(1080,720);
		//Launches the application.
		new Lwjgl3Application(new DawnStarClient(), config);
	}
}
