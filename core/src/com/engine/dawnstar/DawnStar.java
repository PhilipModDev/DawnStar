package com.engine.dawnstar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;

import static com.badlogic.gdx.Gdx.gl32;

public class DawnStar extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private BitmapFont font;
	private FirstPersonCameraController controller;
	private CameraUtils cameraUtils;
	
	@Override
	public void create () {
		cameraUtils = new CameraUtils();
		controller = new FirstPersonCameraController(cameraUtils.getCamera3D());
		Gdx.input.setInputProcessor(controller);

		font = new BitmapFont();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
	}

	@Override
	public void resize(int width, int height) {
	    cameraUtils.resize(width, height);
		controller.update();
	}

	@Override
	public void render () {
		cameraUtils.update();
		controller.update();

		gl32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
		batch.setProjectionMatrix(cameraUtils.getCameraGUI().combined);
		batch.begin();
		font.draw(batch,"FPS:" + Gdx.graphics.getFramesPerSecond(),11, 15);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
