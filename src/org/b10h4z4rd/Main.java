package org.b10h4z4rd;

import org.b10h4z4rd.classviewer.ClassView;
import org.b10h4z4rd.runtime.RuntimeView;

import javax.swing.*;

public class Main {

    public static RuntimeView runtimeView = null;
    public static ClassView classView = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                classView = new ClassView();
            }
        });
    }

    public static void Print(Object toPrint){
        System.out.println(toPrint);
    }
}
