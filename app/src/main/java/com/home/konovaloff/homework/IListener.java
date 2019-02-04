package com.home.konovaloff.homework;

interface IListener {
    void onTaskComplete(DummyTask dummyTask);
    void onProgressUpdate(String txt);
}
