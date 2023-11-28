package com.chupaniko.view;

/**
 * Заглушка для вьюх, которые пока ещё не существуют.
 */
public class PageNotFoundView implements SimpleView {

    /**
     * Starts interaction between the user and the view.
     */
    @Override
    public void show() {
        System.out.println("Запрашиваемый ресурс не найден.");
    }
}
