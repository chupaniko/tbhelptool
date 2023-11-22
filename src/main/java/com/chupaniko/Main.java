package com.chupaniko;

import com.chupaniko.controller.MyHttpClient;
import com.chupaniko.view.MainView;
import com.chupaniko.view.SimpleView;

public class Main {
    public static void main(String[] args) {
        SimpleView view = new MainView();
        view.show();
        MyHttpClient.getInstance().close();
    }
}