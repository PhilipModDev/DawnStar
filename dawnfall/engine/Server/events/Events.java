package com.dawnfall.engine.Server.events;

import com.dawnfall.engine.DawnStar;

public final class Events {
    private final DawnStar dawnStar;
    public ServerPortEvent SERVER_PORT;
    public WorldTickEvent WORLD_TICK;
    public Events(DawnStar dawnStar){
        this.dawnStar = dawnStar;
        registerAllEvents();
    }
    private void registerAllEvents(){
        SERVER_PORT= new ServerPortEvent(0);
        WORLD_TICK = new WorldTickEvent(1);
    }
}
