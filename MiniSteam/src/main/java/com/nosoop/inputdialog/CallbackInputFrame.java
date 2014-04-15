/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nosoop.inputdialog;

import javax.swing.JFrame;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class CallbackInputFrame<T> extends JFrame {
    
    protected Callback<T> callback;
    
    public abstract class Callback<T> {
        public abstract void run(T returnValue);
    }
    
    public CallbackInputFrame(Callback<T> callback) {
        super();
        this.callback = callback;
    }
}
