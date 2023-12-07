package com.dawnfall.engine.Server.util;


import java.util.ArrayList;
import java.util.function.Supplier;

public class DeferredRegister<T> {
    private final ArrayList<Supplier<T>> deferredList = new ArrayList<>();
    private final String contextId;
    private DeferredRegister(String contextId){
        this.contextId = contextId;
    }
    public static <T> DeferredRegister<T> createRegister(String contextId){
        return new DeferredRegister<>(contextId);
    }
    public T register(Supplier<T> supplier,int id){
        byte index = (byte) id;
        deferredList.add(index,supplier);
        return supplier.get();
    }
    public Supplier<T> getRegistry(byte id){
        return deferredList.get(id);
    }
    public String getContextId() {
        return contextId;
    }
}
