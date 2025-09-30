package com.easy.easybook.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {
    
    private static final String TAG = "MyWorker";
    
    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Performing long running task in scheduled job");
        
        // TODO: Add long running task here.
        try {
            // Simulate work
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            return Result.failure();
        }
        
        return Result.success();
    }
}
