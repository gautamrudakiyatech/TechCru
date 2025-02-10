package com.tc.tech.cru.techcrunch.techcru;

import android.content.Context;
import android.content.Intent;

public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context context;

    public CustomExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // Start the error activity
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.putExtra("error", e.getMessage());
        context.startActivity(intent);

        // Terminate the current process
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}

