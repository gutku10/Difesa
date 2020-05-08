/*
package com.skull1.hackathon1.Front;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

*/
/**
 * Handle All Threads.
 *//*

public class AppExecutor {

    private final Executor diskIO;

    private final Executor networkIO;

    private final Executor mainThread;

    private AppExecutor(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public AppExecutor() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}
*/
