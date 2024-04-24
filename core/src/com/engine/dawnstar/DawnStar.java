package com.engine.dawnstar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.engine.dawnstar.main.GameAsset;
import com.engine.dawnstar.utils.CameraUtils;

import static com.badlogic.gdx.Gdx.gl32;

public class DawnStar extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture img;
	private BitmapFont font;
	private FirstPersonCameraController controller;
	public CameraUtils cameraUtils;
	public GameAsset gameAsset;
	private ModelBatch modelBatch;
	private ModelInstance modelInstance;
	private ModelBuilder modelBuilder;
	
	@Override
	public void create () {
		gameAsset = new GameAsset();
		gameAsset.loadAssets(true);
		//Creates a new camera.
		cameraUtils = new CameraUtils();
		controller = new FirstPersonCameraController(cameraUtils.getCamera3D());
		Gdx.input.setInputProcessor(controller);
        //Creates a new font.
		font = gameAsset.getMainFont();
		batch = new SpriteBatch();
		img = gameAsset.getBadLogicGames();

		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,Color.DARK_GRAY));
		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked;
		Model model = modelBuilder.createLineGrid(4096,4096,1,1,material,attributes);
		modelInstance = new ModelInstance(model);
	}

	//Resizes the viewport and the camera.
	@Override
	public void resize(int width, int height) {
	    cameraUtils.resize(width, height);
		controller.update();
	}

	@Override
	public void render () {
		//Updates input and camera.
		toggleFullScreenMode();
		cameraUtils.update();
		controller.update();
        //Calls OpenGL.
		gl32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
		gl32.glClearColor(0.65f, 0.81f, 0.90f, 1.0f);
		//Renders the scene.
		modelBatch.begin(cameraUtils.getCamera3D());
		modelBatch.render(modelInstance);
		modelBatch.end();

		batch.setProjectionMatrix(cameraUtils.getCameraGUI().combined);
		batch.begin();
		//Draws the frames per second.
		drawFpsCounter(batch);
		//Draws any ui stuff.
		batch.draw(img,0,0);
		batch.end();
	}

	//Dispose resources.
	@Override
	public void dispose () {
		batch.dispose();
		gameAsset.dispose();
		modelBatch.dispose();
	}

    //Renders and displays different colors of fps.
	private void drawFpsCounter(Batch batch){
		//Gets the fps count.
		int fps = Gdx.graphics.getFramesPerSecond();
		//Set red if less than 30.
		if (fps < 30) font.setColor(Color.RED);
		//Set orange if less or equal to 45 and greater than or equal to 30.
		if (fps <= 45 && fps >= 30) font.setColor(Color.ORANGE);
		//Set green if greater than 45.
		if (fps > 45) font.setColor(Color.GREEN);
		//Renders the font at the top left.
		font.draw(batch,"FPS:" + Gdx.graphics.getFramesPerSecond(),50,
				cameraUtils.getCameraGUI().viewportHeight - 50);
	}

	//Sets the display mode to be if full screen or windowed.
	private void toggleFullScreenMode(){
	  if (Gdx.input.isKeyJustPressed(Input.Keys.F11)){
		  if (Gdx.graphics.isFullscreen()) {
			  Gdx.graphics.setWindowedMode(1080,720);
			  return;
		  }
		  Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
	  }
	}
}
