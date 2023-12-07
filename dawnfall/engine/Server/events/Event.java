package com.dawnfall.engine.Server.events;

public abstract class Event {

   public final byte id;
   public Event(int id){
      this.id = (byte) id;
   }
   abstract void fireEvent();
   abstract byte  getIdSource();
}
