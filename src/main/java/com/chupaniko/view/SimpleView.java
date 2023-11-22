package com.chupaniko.view;

/**
 * Contract for views that print something to console.
 *
 * @author Nikolai Chupakhin
 * @version 1.0
 * @since 1.0
 */
public interface SimpleView {

    /**
     * Starts interaction between the user and the view.
     */
    void show();
}