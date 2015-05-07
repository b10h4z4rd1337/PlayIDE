package org.b10h4z4rd.runtime.view;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.classviewer.ClassItem;
import org.b10h4z4rd.runtime.HelperUtils;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 03.05.15.
 */
public class RuntimeView extends JFrame{

    private JVMDebugger debugger;
    private TerminalView tv;
    private Map<String, ObjectItem> objList;

    public RuntimeView() {
        setLayout(null);
        debugger = Main.debugger;
        tv = new TerminalView();
        debugger.redirectStreams(tv.createNewOutputStream(), tv.createNewOutputStream());

        objList = new HashMap<>();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        getContentPane().setBackground(Color.WHITE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setAutoRequestFocus(true);
        setTitle("Java Virtual Machine");
    }

    public void newObject(ClassItem classItem, Method constructor, String objName){
        if (!objList.containsKey(objName)) {
            if (HelperUtils.needsArguments(constructor))
                new ParameterInputView(classItem.getClassName(), constructor, objName);
            else {
                ObjectItem newItem = new ObjectItem(classItem.getClassName(), constructor, objName);
                objList.put(objName, newItem);
                newItem.setLocation(600 / 2 - ObjectItem.WIDTH / 2, 400 / 2 - ObjectItem.HEIGHT + 25);
                add(newItem);
                repaint();
            }
        }else {
            JOptionPane.showMessageDialog(null, "An Object with this name already exists!", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addObject(String objName, ObjectReference or){
        ObjectItem newItem = new ObjectItem(objName, or);
        objList.put(objName, newItem);
        newItem.setLocation(600 / 2 - ObjectItem.WIDTH / 2, 400 / 2 - ObjectItem.HEIGHT + 25);
        add(newItem);
        repaint();
    }

    public void removeObject(ObjectItem objectItem){
        try {
            debugger.removeObject(objectItem.getObjectReference());
            objList.remove(objectItem.getObjectName());
            remove(objectItem);
            repaint();
        } catch (InvalidTypeException | ClassNotLoadedException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ObjectItem> getObjList(){
        return objList;
    }

    @Override
    public void dispose() {
        try {
            debugger.restart();
        } catch (ClassNotLoadedException | IncompatibleThreadStateException | InvalidTypeException e) {
            e.printStackTrace();
        }
        tv.dispose();
        Main.runtimeView = null;
        super.dispose();
    }
}
