package com.home.konovaloff.homework;

import android.os.AsyncTask;

public class DummyTask extends AsyncTask <Object, String, Boolean>{
    private static final String TAG = DummyTask.class.getSimpleName();
    private static final byte MAX_ITERATION = 10;
    private static final short DELAY_IN_MS = 1000;

    private IListener listener;
    private String lastError = "";

    public String getLastError(){
        return lastError;
    }

    public DummyTask(){

    }

    public void setListener(IListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        //Делаем подготовительные работы. Например, инициализацию прогресс-бара
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (listener != null){
            listener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected Boolean doInBackground(Object[] objects) {
        publishProgress("Подготовка");

        try {
            for (int i = 0; i < MAX_ITERATION; i++){
                publishProgress(String.format("Выполнено: %s%%", Integer.toString(i)));
                Thread.sleep(DELAY_IN_MS);
            }
        }catch (Exception e){
//            Global.log_e(TAG, e.toString());
            lastError = e.toString();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (listener != null)
            listener.onTaskComplete(this);
    }
}
