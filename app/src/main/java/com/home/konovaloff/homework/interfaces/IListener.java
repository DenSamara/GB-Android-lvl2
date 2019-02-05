package com.home.konovaloff.homework.interfaces;

import com.home.konovaloff.homework.tasks.DummyTask;

public interface IListener {
    void onTaskComplete(DummyTask dummyTask);
    void onProgressUpdate(String txt);
}
