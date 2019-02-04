package com.home.konovaloff.homework.tasks;

import android.app.IntentService;
import android.content.Intent;

public class DummyIntentService extends IntentService {
    public static final byte RESULT_SUCCESS = Byte.MAX_VALUE;
    public static final byte RESULT_ERROR = Byte.MIN_VALUE;
    public static final String BROADCAST_ACTION = "com.home.konovaloff.homework.tasks.dummyIntentService";
    private static final short DELAY_IN_MS = 1000;

    public static String EXTRA_REPEAT_COUNT = "DummyIntentService.extra.repeat.count";
    public static String EXTRA_RESULT = "DummyIntentService.extra.result";
    public static String EXTRA_MESSAGE = "DummyIntentService.extra.message";

    private String lastError = "";

    public DummyIntentService() {
        super(DummyIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        byte status = RESULT_SUCCESS;
        int repeatCount = intent.getIntExtra(EXTRA_REPEAT_COUNT, 5);
        try {
            for (int i = 0; i < repeatCount; i++){
                Thread.sleep(DELAY_IN_MS);
            }
        }catch (Exception e){
            lastError = e.toString();
            status = RESULT_ERROR;
        }

        Intent result = new Intent(BROADCAST_ACTION);
        result.putExtra(EXTRA_RESULT, status);
        result.putExtra(EXTRA_MESSAGE, lastError);
        sendBroadcast(result);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
