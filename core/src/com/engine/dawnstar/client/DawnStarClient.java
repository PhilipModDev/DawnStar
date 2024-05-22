package com.engine.dawnstar.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
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
import com.engine.dawnstar.main.GameAsset;
import com.engine.dawnstar.main.blocks.Blocks;
import com.engine.dawnstar.main.data.World;
import com.engine.dawnstar.server.DawnStarServer;
import com.engine.dawnstar.utils.CameraUtils;
import com.engine.dawnstar.utils.ShaderUtils;
import com.engine.dawnstar.utils.io.ResourceLocation;
import com.engine.dawnstar.utils.math.Direction;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import static com.badlogic.gdx.Gdx.gl32;

public class DawnStarClient extends ApplicationAdapter {

	public static final Logger LOGGER = LogManager.getLogger(DawnStarClient.class);
	public static final int RENDER_DISTANCE = 20;
	public static final int LOD_DISTANCE = 20;
	public DawnStarServer dawnStarServer;
	public final ClientUser clientUser;
	public ClientConnection clientConnection;
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
	private static DawnStarClient dawnStarClient;
	private final ResourceLocation resourceLocation;
	private final GridPoint3 playerPos = new GridPoint3();
	private final GridPoint3 playerChunkPos = new GridPoint3();
	private ChunkRender chunkRender;

	public DawnStarClient(){
		resourceLocation = new ResourceLocation();
		clientUser = new ClientUser("PhilipModDev");
		dawnStarClient = this;
		Configurator.setRootLevel(Level.INFO);
	}

	private static void localConnection(){
		dawnStarClient.clientConnection = new ClientConnection();
		Thread client = new Thread(() ->{
			EventLoopGroup threads = new NioEventLoopGroup(run -> {
				Thread thread = new Thread(run);
				thread.setDaemon(true);
				thread.setName("client-worker");
				return thread;
			});
			Bootstrap clientBoot = new Bootstrap();
			try {
				clientBoot.group(threads);
				clientBoot.channel(NioSocketChannel.class);
				clientBoot.remoteAddress(new InetSocketAddress(InetAddress.getLocalHost(),3868));
				clientBoot.handler(dawnStarClient.clientConnection);
				ChannelFuture future = clientBoot.connect().sync();
				future.channel().closeFuture().sync();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				threads.shutdownGracefully();
			}
		});
		client.setName("client");
		client.setDaemon(true);
		client.start();
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
		controller.setVelocity(60f);
		Gdx.input.setInputProcessor(controller);
        //Creates a new font.
		font = gameAsset.getMainFont();
		batch = new SpriteBatch();

		modelBatch = new ModelBatch();
		modelBuilder = new ModelBuilder();
		Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse,Color.WHITE));

		int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked;
		Model model = modelBuilder.createXYZCoordinates(32,material,attributes);
		modelInstance = new ModelInstance(model);

		shaderUtils = new ShaderUtils(cameraUtils);
		chunkRender = new ChunkRender(this);
		World world = new World();
		if (world.isDoneLoading()){
			dawnStarServer = DawnStarServer.create(()-> new DawnStarServer(world));
		}
		localConnection();
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
		final Vector3 pos = cameraUtils.getCamera3D().position;
		playerChunkPos.set(
				MathUtils.floor(pos.x) >> 4,
				MathUtils.floor(pos.y) >> 4,
				MathUtils.floor(pos.z) >> 4
		);
        //Calls OpenGL.
		gl32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT);
		gl32.glClearColor(0.5f, 0.5f, 0.55f, 1.0f);
		//Renders the scene.
		modelBatch.begin(cameraUtils.getCamera3D());
		modelBatch.render(modelInstance);
		modelBatch.end();

		chunkRender.render();

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
	public GridPoint3 getPlayerPos(){
		Camera camera = cameraUtils.getCamera3D();
		int x = MathUtils.floor(camera.position.x / 0.5f);
		int y = MathUtils.floor(camera.position.y / 0.5f);
		int z = MathUtils.floor(camera.position.z / 0.5f);
        return playerPos.set(x,y,z);
	}

	public GridPoint3 getPlayerChunkPos(){
		return playerChunkPos;
	}

	//Dispose resources.
	@Override
	public void dispose () {
		chunkRender.dispose();
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
						"\nPosition " + getPlayerPos() + "\nPlayer Chunk Position: "+ getPlayerChunkPos() ,50,
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

	public static DawnStarClient getInstance(){
		return dawnStarClient;
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public ChunkRender getChunkRender() {
		return chunkRender;
	}
}
