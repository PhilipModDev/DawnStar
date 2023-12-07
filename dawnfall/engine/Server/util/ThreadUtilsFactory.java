package com.dawnfall.engine.Server.util;

import com.dawnfall.engine.Server.util.IO.OptionProvider;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ThreadUtilsFactory {
   public  ExecutorService SINGLE_THREAD;
   private ThreadPoolExecutor THREADED_EXECUTOR;
   public ThreadUtilsFactory(){
       SINGLE_THREAD = Executors.newFixedThreadPool(1, r -> {
           Thread shutDownThread = Executors.defaultThreadFactory().newThread(r);
           shutDownThread.setDaemon(true);
           return shutDownThread;
       });
   }
   public ThreadUtilsFactory(int minThreads, int maxThreads, int maxTaskPool, int aliveTime){
       ArrayBlockingQueue<Runnable> tasks = new ArrayBlockingQueue<>(maxTaskPool);
       this.THREADED_EXECUTOR = new ThreadPoolExecutor(minThreads,maxThreads,aliveTime,TimeUnit.MILLISECONDS, tasks);
    }
    public void execute(Runnable task){
       if (OptionProvider.THREADED_LOADER) THREADED_EXECUTOR.execute(task);
       else SINGLE_THREAD.execute(task);
    }

    public synchronized <T> void addValueToQueue(Queue<T> queue, T value){
        queue.add(value);
    }
    public synchronized <T> T removeValueToQueue(Queue<T> queue){
        return queue.poll();
    }
    public synchronized <T extends Queue<?>> void synchronizedQueueTask(T queue,Consumer<T> task){
       task.accept(queue);
    }
}
