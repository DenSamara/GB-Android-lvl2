package com.home.konovaloff.homework.tasks;

import android.os.AsyncTask;

import com.home.konovaloff.homework.MyApp;
import com.home.konovaloff.homework.R;
import com.home.konovaloff.homework.interfaces.IListener;

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
        publishProgress(MyApp.getContext().getString(R.string.prepare));

        try {
            for (int i = 0; i < MAX_ITERATION; i++){
                publishProgress(String.format("%s: %s%%", MyApp.getContext().getString(R.string.done), Integer.toString(i*10)));
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
