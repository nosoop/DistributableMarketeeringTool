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
    
    protected DialogCallback<T> callback;
    
    public static abstract class DialogCallback<T> {
        public abstract void run(T returnValue);
    }
    
    public CallbackInputFrame(DialogCallback<T> callback) {
        super();
        this.callback = callback;
    }
}
