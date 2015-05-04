package org.b10h4z4rd.runtime;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.classviewer.ClassItem;
import org.b10h4z4rd.runtime.debugger.ConnectorNotFoundException;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 03.05.15.
 */
public class RuntimeView extends JFrame{

    private JVMDebugger debugger;
    private Map<String, ObjectItem> objList;

    public RuntimeView(){
        debugger = new JVMDebugger(new File("/Users/Mathias/Documents/IdeaProjects/PlayIDE/out/production/PlayIDE/"), 7000);

        try {
            debugger.launch(System.out, System.err);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ConnectorNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalConnectorArgumentsException e) {
            e.printStackTrace();
        }

        objList = new HashMap<String, ObjectItem>();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
        setVisible(true);
    }

    public void addObject(ClassItem classItem, String name){
        ObjectItem newItem = new ObjectItem(classItem.getClassName(), name);
        objList.put(name, newItem);
        add(newItem);
        repaint();
    }

    public void removeObject(ObjectItem objectItem){
        try {
            debugger.removeObject(objectItem.getObjectName());
            objList.remove(objectItem.getObjectName());
            remove(objectItem);
            repaint();
        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        }
    }

    public JVMDebugger getDebugger(){
        return debugger;
    }

    @Override
    public void dispose() {
        debugger.exit();
        Main.runtimeView = null;
        super.dispose();
    }
}
