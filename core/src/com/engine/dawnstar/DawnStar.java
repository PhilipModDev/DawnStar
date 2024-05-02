package com.engine.dawnstar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
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
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.engine.dawnstar.main.GameAsset;
import com.engine.dawnstar.main.Quad;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.utils.CameraUtils;

import java.util.Arrays;

import static com.badlogic.gdx.Gdx.gl32;

public class DawnStar extends ApplicationAdapter {
	private SpriteBatch batch;
	private BitmapFont font;
	private FirstPersonCameraController controller;
	public CameraUtils cameraUtils;
	public GameAsset gameAsset;
	private ModelBatch modelBatch;
	private ModelInstance modelInstance;
	private ModelBuilder modelBuilder;
	public ShaderProgram shaderProgram;
	public IndexData indexData;
	private static DawnStar dawnStar;
	private Array<Quad> quads;

	public DawnStar(){
		dawnStar = this;
	}
	
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

		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,Color.DARK_GRAY));
		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked;
		Model model = modelBuilder.createLineGrid(4096,4096,1,1,material,attributes);
		modelInstance = new ModelInstance(model);
		buildIndices();

		FileHandle vertex = Gdx.files.internal("shaders/vertex.glsl");
		FileHandle fragment = Gdx.files.internal("shaders/fragment.glsl");

		shaderProgram = new ShaderProgram(vertex,fragment);
		quads = new Array<>();

		for (int i = 0; i < 1000; i++) {
			Quad quad = new Quad(gameAsset.findRegion("quad").get(),0,i,0);
			quads.add(quad);
		}
		Chunk chunk = new Chunk(0,0,0);
        chunk.setBlock(10,11,13,(byte) 1);
		chunk.setBlock(10,12,13,(byte) 11);
		chunk.setBlock(10,12,13,(byte) 12);
		System.out.println(chunk.getBlock(10,11,13));
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

		//Binds for voxel rendering.
		gameAsset.getAtlasTexture().bind();

		for (int i = 0; i < quads.size; i++) {
			Quad quad = quads.get(i);
			quad.render(cameraUtils.getCamera3D());
		}

        //Unbinds for voxel rendering.
		gl32.glDisable(GL32.GL_DEPTH_TEST);
		gl32.glDisable(GL32.GL_CULL_FACE);


		batch.setProjectionMatrix(cameraUtils.getCameraGUI().combined);
		batch.begin();
		//Draws the frames per second.
		drawFpsCounter(batch);
		//Draws any ui stuff.
        //batch.draw(img,0,0);
		batch.end();
	}

	//Dispose resources.
	@Override
	public void dispose () {
		batch.dispose();
		gameAsset.dispose();
		modelBatch.dispose();
		quads.spliterator().tryAdvance(Quad::dispose);
		shaderProgram.dispose();
		indexData.dispose();
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

	private void buildIndices(){
		int maxIndices = 90000;
		indexData = new IndexArray(maxIndices);
		short[] indices = new short[maxIndices];
		for (int i = 0, v = 0; i < maxIndices; i += 6, v += 4) {
			indices[i] = (short)v;
			indices[i+1] = (short)(v+1);
			indices[i+2] = (short)(v+2);
			indices[i+3] = (short)(v+2);
			indices[i+4] = (short)(v+3);
			indices[i+5] = (short) v;
		}
		indexData.setIndices(indices,0,indices.length);
	}

	public static DawnStar getInstance(){
		return dawnStar;
	}
}
