package com.engine.dawnstar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.engine.dawnstar.main.ChunkBuilder;
import com.engine.dawnstar.main.GameAsset;
import com.engine.dawnstar.main.blocks.Blocks;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.ChunkPos;
import com.engine.dawnstar.main.mesh.ChunkMesh;
import com.engine.dawnstar.utils.CameraUtils;
import com.engine.dawnstar.utils.ShaderUtils;
import com.engine.dawnstar.utils.io.ResourceLocation;
import com.engine.dawnstar.utils.math.Direction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.badlogic.gdx.Gdx.gl32;

public class DawnStar extends ApplicationAdapter {

	public static final Logger LOGGER = LogManager.getLogger(DawnStar.class);
	public GLProfiler glProfiler;
	public Blocks blocks;
	private SpriteBatch batch;
	private BitmapFont font;
	private FirstPersonCameraController controller;
	public CameraUtils cameraUtils;
	public GameAsset gameAsset;
	private ModelBatch modelBatch;
	private ModelInstance modelInstance;
	private ModelBuilder modelBuilder;
	public ShaderUtils shaderUtils;
	private static DawnStar dawnStar;
	private final ResourceLocation resourceLocation;
	private Hashtable<ChunkPos,Chunk> chunks;
	private Array<ChunkMesh> chunkMeshes;

	public DawnStar(){
		resourceLocation= new ResourceLocation();
		dawnStar = this;
		Configurator.setRootLevel(Level.ALL);
	}
	
	@Override
	public void create () {
		glProfiler = new GLProfiler(Gdx.graphics);
		glProfiler.enable();
		//Loads all assets.
		gameAsset = new GameAsset();
		gameAsset.loadAssets(true);

		blocks = new Blocks();
		blocks.registerBlocks();
		//Creates a new camera.
		cameraUtils = new CameraUtils();
		controller = new FirstPersonCameraController(cameraUtils.getCamera3D());
		controller.setVelocity(40f);
		Gdx.input.setInputProcessor(controller);
        //Creates a new font.
		font = gameAsset.getMainFont();
		batch = new SpriteBatch();

		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,Color.WHITE));

		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked;
		Model model = modelBuilder.createXYZCoordinates(10,material,attributes);
		modelInstance = new ModelInstance(model);

		shaderUtils = new ShaderUtils(cameraUtils);

		Random random = new Random();
		chunks = new Hashtable<>();
		chunkMeshes = new Array<>();
		ChunkBuilder chunkBuilder = new ChunkBuilder(chunks);
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 5; z++) {
					Chunk chunk = new Chunk(x ,y ,z );
					for (int xi = 0; xi < Chunk.SIZE; xi++) {
						for (int yi = 0; yi < Chunk.SIZE; yi++) {
							for (int zi = 0; zi < Chunk.SIZE; zi++) {
								chunk.setBlock(xi, yi, zi, (byte) 1);
							}
						}
					}
					chunk.setBlock(0, 0, 0, (byte) 0);
					chunks.put(new ChunkPos(chunk.localX ,chunk.localY ,chunk.localZ),chunk);
				}
			}
		}

		Set<Map.Entry<ChunkPos,Chunk>> entries = chunks.entrySet();
		entries.forEach((entry) ->{
			ChunkMesh chunkMesh = chunkBuilder.create(entry.getValue()).orElseThrow();
			chunkMeshes.add(chunkMesh);
		});
	}

	public final Vector3 chunkCenter = new Vector3();
	//Holds the chunk sizes.
	private final Vector3 chunkDimensions = new Vector3(Chunk.SIZE,Chunk.SIZE,Chunk.SIZE);
	public boolean isChunkVisible(Chunk chunk){
		Camera camera  = cameraUtils.getCamera3D();
		int chunkHalfDimension = (Chunk.SIZE / 2);
		chunkCenter.set(
				(chunk.localX*Chunk.SIZE) + chunkHalfDimension,
				(chunk.localY*Chunk.SIZE) + chunkHalfDimension,
				(chunk.localZ*Chunk.SIZE) + chunkHalfDimension
		);
		chunkCenter.sub(camera.position).nor();
		double fov = MathUtils.cos((cameraUtils.getCamera3D().fieldOfView + 15) * MathUtils.degreesToRadians);
		double dot = camera.direction.dot(chunkCenter);
		return fov <= dot;
	}

	//Resizes the viewport and the camera.
	@Override
	public void resize(int width, int height) {
	    cameraUtils.resize(width, height);
		controller.update();
	}

	@Override
	public void render () {
		glProfiler.reset();

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
		shaderUtils.bindTerrain();

		for (int i = 0; i <chunkMeshes.size ; i++) {
			ChunkMesh chunkMesh = chunkMeshes.get(i);
            if (isChunkVisible(chunkMesh.chunk)) chunkMesh.render();
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


	public Direction getFacingDirection(){
		Vector3 direction = cameraUtils.getCamera3D().direction;
		if (Math.round(direction.y) >= 0.5f) return Direction.UP;
		if (Math.round(direction.y) <= -0.5f) return Direction.DOWN;
		if (Math.round(direction.x) >= 0.5f) return Direction.EAST;
		if (Math.round(direction.x) <= -0.5f) return Direction.WEST;
		if (Math.round(direction.z) >= 0.5f) return Direction.NORTH;
		if (Math.round(direction.z) <= -0.5f) return Direction.SOUTH;
		return null;
	}
	public GridPoint3 getPlayPos(){
		Camera camera = cameraUtils.getCamera3D();
		int x = MathUtils.floor(camera.position.x);
		int y = MathUtils.floor(camera.position.y);
		int z = MathUtils.floor(camera.position.z);
        return new GridPoint3(x,y,z);
	}

	//Dispose resources.
	@Override
	public void dispose () {
		chunkMeshes.forEach((ChunkMesh::dispose));
		batch.dispose();
		gameAsset.dispose();
		modelBatch.dispose();
		shaderUtils.dispose();
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
		font.draw(batch,"FPS:" + Gdx.graphics.getFramesPerSecond() ,50,
				cameraUtils.getCameraGUI().viewportHeight - 50);
		font.setColor(Color.WHITE);
		font.draw(batch,"Draw Calls:" + glProfiler.getDrawCalls() +"\nDirection:"+getFacingDirection() +
						"\nPosition " + getPlayPos() ,50,
				cameraUtils.getCameraGUI().viewportHeight - 80);
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

	public static DawnStar getInstance(){
		return dawnStar;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}
}
