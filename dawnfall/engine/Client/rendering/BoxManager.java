package com.dawnfall.engine.Client.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.Server.util.VoxelRayCast;
import com.dawnfall.engine.Server.util.BlockPos;
import com.dawnfall.engine.Server.util.Camera;
import com.dawnfall.engine.Server.world.World;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;

import static com.badlogic.gdx.Gdx.gl;

public class BoxManager implements Disposable {
	private final Camera cam;
	private final ShapeRenderer shape;
	private final World world;
	public BoxManager(Camera cam, World world) {
		this.world = world;
		this.cam = cam;
		shape = new ShapeRenderer(32);
		shape.setAutoShapeType(true);
		shape.setColor(Color.GRAY);
	}
	// 10754
	public void render(final VoxelRayCast.RayInfo ray) {
		gl.glLineWidth(2);
		if (ray == null) return;
		final BlockPos pos = ray.in;
		shape.setColor(Color.GRAY);
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeRenderer.ShapeType.Line);
		shape.box(pos.x-0.01f, pos.y-0.01f, pos.z+1.01f, 1.02f, 1.02f, 1.02f);
		shape.end();
	}
	public void renderChunkBorder(){
		gl.glLineWidth(2);
		shape.setColor(Color.BLUE);
		Chunk chunk = world.getChunkAtPlayer();
		if (chunk == null) return;
		shape.setProjectionMatrix(cam.combined);
		shape.begin(ShapeRenderer.ShapeType.Line);
		shape.box(cam.position.x-8,cam.position.y-8 ,cam.position.z+8,16, 16, 16);
		shape.end();
	}
	
	@Override
	public void dispose() {
		shape.dispose();
	}
}
