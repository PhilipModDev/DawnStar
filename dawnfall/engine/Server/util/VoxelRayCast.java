package com.dawnfall.engine.Server.util;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.dawnfall.engine.Server.entity.Player;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.World;

import static com.badlogic.gdx.math.MathUtils.floor;

public final class VoxelRayCast {
    private final float LENGTH = 8.0f;
    private final float STEPS = 0.05f;
    private final RayInfo info = new RayInfo();
    private final Vector3 pos = new Vector3();
    private final Vector3 nor = new Vector3();
    private final GridPoint3 offset = new GridPoint3();
    private final World world = World.getWorld();
    public synchronized RayInfo ray(Player player) {
        Camera cam = player.getCamera();
        nor.set(cam.direction).nor().scl(STEPS);
        Vector3 camPos = cam.position;
        offset.set(floor(camPos.x), floor(camPos.y), floor(camPos.z));
        pos.set(camPos).sub(offset.x, offset.y, offset.z);
        int x, y  = 0, z  = 0; // Current integer position.
        int lx = 0, ly = 0, lz = 0; // Last integer position.
        for (float i = 0; i < LENGTH; i += STEPS) {
            pos.add(nor);
            if ((x = floor(pos.x)) == lx && (y = floor(pos.y)) == ly && (z = floor(pos.z)) == lz) continue;

            if (world.getBlock(x+offset.x, y+offset.y, z+offset.z) == Blocks.AIR ||
                    world.getBlock(x+offset.x, y+offset.y, z+offset.z) == Blocks.CAVE_AIR){
                lx = x; ly = y; lz = z;
                continue;
            }
            info.in.set(x, y, z).add(offset);
            info.out.set(lx, ly, lz).add(offset);
            return info;
        }
        return null;
    }
    public static class RayInfo {
        /** Outside the block. */
        public final BlockPos out = new BlockPos();
        /** Inside the block. */
        public final BlockPos in  = new BlockPos();
    }
}
