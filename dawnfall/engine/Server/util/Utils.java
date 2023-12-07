package com.dawnfall.engine.Server.util;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public class Utils {
    public static FileHandle getInternalFile(String name) {
        return Gdx.files.internal(name);
    }

    public static FileHandle getExternalFile(String name) {
        return Gdx.files.external(name);
    }

    public static FileHandle getAbsoluteFile(String name) {
        return Gdx.files.absolute(name);
    }

    public static FileHandle getClasspathFile(String name) {
        return Gdx.files.classpath(name);
    }

    public static FileHandle getLocalFile(String name) {
        return Gdx.files.local(name);
    }

    public static FileHandle getFileHandle(String name, Files.FileType fileType) {
        return Gdx.files.getFileHandle(name, fileType);
    }

    public static boolean isNullCollection(Collection<Object> collection, Object object) {
        return !collection.contains(object);
    }

    public static FileHandle getFile(String path){
        return getInternalFile(path);
    }

    /**
     * Fast floor for double.
     */
    public static int floor(final double x) {
        final int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }

    /**
     * Null-safe dispose method for disposable object.
     */
    public static void disposes(Disposable dis) {
        if (dis != null) dis.dispose();
    }

    /**
     * Null-safe disposes method for disposable objects.
     */
    public static void disposes(Disposable... dis) {
        final int size = dis.length;
        for (final Disposable d : dis) {
            if (d != null) d.dispose();
        }
    }

    /**
     * Resets the mouse position to the center of the screen.
     */
    public static void resetMouse() {
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
    }

    /**
     * Seconds to 60 tick update.
     */
    public static int seconds(int second) {
        return second * 60;
    }

    /**
     * Milliseconds to 60 tick update.
     */
    public static int milliseconds(int mils) {
        return MathUtils.roundPositive((mils / 1000f) * 60f);
    }

    /**
     * Utility log.
     */
    public static void log(Object tag, Object obj) {
        if (tag instanceof Class) {
            Gdx.app.log(((Class<?>) tag).getSimpleName(), obj.toString());
        } else {
            Gdx.app.log(tag.toString(), obj.toString());
        }
    }

   public static int xyzToIndex(int x, int y , int z, int max){
       return (z * max * max) + (y * max) + x;
   }
    public static int xyzToIndex_2d(int x, int z, int width){
        return width * x + z;
    }
    public static int xyzToIndex(int x, int y , int z){
        return  x | y << 8 | z << 4;
    }

    public static int getRandomNumber(int size) {
        RandomXS128 randomXS128 = new RandomXS128();
        return randomXS128.nextInt(size);
    }
    public static int percentageCounter(int percentage) {
        if (percentage <= 100){
            return 1;
        }else if(percentage < 200){
           return 20;
        }else if(percentage < 300){
            return 30;
        }else if(percentage < 400){
            return 40;
        }else if(percentage < 500){
            return 50;
        }else if(percentage < 600){
            return 60;
        }else if(percentage < 700){
            return 70;
        }else if(percentage < 800){
            return 80;
        }else if(percentage < 900){
            return 90;
        }else if(percentage < 1000){
            return 100;
        }
        return 0;
    }
    public static <T> boolean indexInList(List<T> list, int index){
        try {
           T type = list.get(index);
           if (type != null) return true;
        }catch (Exception exception){
            return false;
        }
        return false;
    }
    public static byte[][][] split3DArray(byte[][][] array, int subDivisions){
        return Arrays.copyOfRange(array,0,array.length/subDivisions);
    }

    public static  <T> T function(Supplier<T> function){
        return function.get();
    }
}
