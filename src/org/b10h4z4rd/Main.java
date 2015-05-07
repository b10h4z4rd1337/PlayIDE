package org.b10h4z4rd;

import org.b10h4z4rd.classviewer.ClassView;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;
import org.b10h4z4rd.runtime.view.RuntimeView;

import javax.swing.*;

public class Main {

    public static RuntimeView runtimeView = null;
    public static ClassView classView = null;
    public static JVMDebugger debugger = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> classView = new ClassView());
    }

    public static void Print(Object toPrint){
        System.out.println(toPrint);
    }
}
