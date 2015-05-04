package org.b10h4z4rd;

import org.b10h4z4rd.classviewer.ClassView;
import org.b10h4z4rd.runtime.RuntimeView;

import javax.swing.*;

public class Main {

    public static RuntimeView runtimeView = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        ClassView classView = new ClassView();
        classView.setSize(600, 400);
        frame.setContentPane(classView);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void Print(Object toPrint){
        System.out.println(toPrint);
    }
}
