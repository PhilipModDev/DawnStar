package com.engine.dawnstar.utils;

import java.util.function.Supplier;

public class Registry<T> {
    //RegistryObject for the result of the supplier.
    private final T registryObject;
    //Registry constructor.
    protected Registry(Supplier<T> registrySupplier){
        this.registryObject = registrySupplier.get();
    }

    //Used to create a registry.
   public static <R> Registry<R> create(Supplier<R> supplier){
        return new Registry<R>(supplier);
   }

   //Used to get a registry object.
    public T get() {
        return registryObject;
    }
}
