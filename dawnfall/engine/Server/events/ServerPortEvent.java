package com.dawnfall.engine.Server.events;

import com.badlogic.gdx.Gdx;

public class ServerPortEvent extends Event {
    public ServerPortEvent(int id) {
        super(id);
    }
    /**
     * This event will only fire when a server has been ported.
     */
    @Override
    public void fireEvent() {
        Gdx.app.log("Server","Server has been ported.");

    }

    @Override
    public byte getIdSource() {
        return id;
    }
}
