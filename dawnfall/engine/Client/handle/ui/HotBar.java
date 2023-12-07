package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.world.Blocks.Block;
import com.dawnfall.engine.Server.world.Blocks.BlockType;
import com.dawnfall.engine.Server.world.Blocks.Blocks;

import static com.dawnfall.engine.Server.world.Blocks.Blocks.*;

public class HotBar extends ScreenAdapter {
     private final DawnStar dawnStar = DawnStar.getInstance();
     private final SpriteBatch spriteBatch = new SpriteBatch();
     private TextureRegion texture;
     private final Camera camera;
     public final int MAX_SLOTS = 9;
     private int slotCounter;
     private boolean isChangedItem = false;
     private byte itemId;
     public HotBar(Camera camera){
          this.camera = camera;
          texture = new TextureRegion(dawnStar.getTextureHolder().getTextureRegion("sand",Block.class));
          slotCounter = SAND;
     }

     @Override
     public void render(float delta) {
          updateHotBar();
          spriteBatch.begin();
          spriteBatch.draw(texture,100,100,100,100);
          spriteBatch.end();
          super.render(delta);
     }
     private void updateHotBar(){
          if (isChangedItem){
               if (slotCounter <= MAX_SLOTS && slotCounter >= 0) {
                    Block block = Blocks.getBlock(slotCounter);
                    if (block != null && block.getBlockType() != BlockType.AIR){
                         String name = blocks[slotCounter].getName();
                         if (name.equals(blocks[AIR].name)) return;
                         if (name.equals(blocks[GRASS].name)){
                              name = "grass_side";
                         }else if (name.equals(blocks[WATER].name)){
                              name = "water1";
                         }
                         itemId = getBlock(slotCounter).id;
                         setItemTexture(name);
                    }
               }
          }
     }
     private void setItemTexture(String name){
          texture = dawnStar.getTextureHolder().getTextureRegion(name,Block.class);
     }

     @Override
     public void resize(int width, int height) {
          camera.viewportWidth = width;
          camera.viewportHeight = height;
          camera.update();
          spriteBatch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
     }

     @Override
     public void dispose() {
          spriteBatch.dispose();
     }

     public int getSlotCounter() {
          return slotCounter;
     }
     public void addSlotCounter(int slotCounter) {
          this.slotCounter += slotCounter;
     }
     public void setSlotCounter(int slotCounter) {
          this.slotCounter = slotCounter;
     }
     public boolean isChangedItem() {
          return isChangedItem;
     }

     public void setChangedItem(boolean changedItem) {
          isChangedItem = changedItem;
     }
     public byte getItemId() {
          return itemId;
     }
}
