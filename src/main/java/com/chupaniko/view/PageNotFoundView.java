package com.chupaniko.view;

public class PageNotFoundView implements SimpleView {
    @Override
    public void show() {
        System.out.println("Запрашиваемый ресурс не найден.");
    }
}
