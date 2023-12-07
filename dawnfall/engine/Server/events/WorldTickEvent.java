package com.dawnfall.engine.Server.events;

public class WorldTickEvent extends Event {
    private int worldTick;
    public WorldTickEvent(int id){
        super(id);
    }
    private void updateWorldTick(){
      if (worldTick >= 50){
          worldTick = 0;
      }
      worldTick++;
    }
    public int getWorldTick() {
        return worldTick;
    }
    @Override
    public void fireEvent() {
       updateWorldTick();
    }
    @Override
    public byte getIdSource() {
        return id;
    }
}
