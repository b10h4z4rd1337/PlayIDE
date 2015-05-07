package org.b10h4z4rd.runtime.view;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 05.05.15.
 */
public class ParameterInputView extends JFrame {

    private String className, objectName;
    private JVMDebugger debugger;
    private ObjectItem objectItem;
    private Method method;
    private List<String> classes;
    private Object[][] data;

    public ParameterInputView(String className, Method method, String objectName){
        this.className = className;
        this.objectName = objectName;
        this.method = method;
        classes = new ArrayList<>();
        debugger = Main.debugger;

        try {
            debugger.loadClass(className);
            init();
        } catch (InvalidTypeException | ClassNotLoadedException | InterruptedException | InvocationException | IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        setTitle("Instantiate Class");
    }

    public ParameterInputView(ObjectItem objectItem, Method method){
        this.objectItem = objectItem;
        this.method = method;

        debugger = Main.debugger;

        try {
            init();
        } catch (InvalidTypeException | ClassNotLoadedException | InterruptedException | InvocationException | IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        setTitle("Invoke Method");
    }

    private void init() throws ClassNotLoadedException, InterruptedException, InvalidTypeException, InvocationException, IncompatibleThreadStateException {
        setLayout(new BorderLayout());
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ButtonListener());

        List<String> args;
        if (method != null)
            args = method.argumentTypeNames();
        else
            args = debugger.loadClass(className).methodsByName("<init>").get(0).argumentTypeNames();

        String[] heads = {"Type", "Value"};
        data = new Object[args.size()][2];
        replacePrimitives(args);
        classes = args;

        for (int i = 0; i < args.size(); i++) {
            Class c;
            try {
                c = Class.forName(args.get(i));
            } catch (ClassNotFoundException e) {
                c = null;
            }
            if (c != null)
                data[i][0] = c.getSimpleName();
            else
                data[i][0] = args.get(i);
            data[i][1] = "";
        }

        JTable params = new JTable(data, heads);
        params.setShowGrid(true);

        getContentPane().add(params.getTableHeader(), BorderLayout.PAGE_START);
        getContentPane().add(params, BorderLayout.CENTER);
        getContentPane().add(okButton, BorderLayout.SOUTH);

        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private static List<String> replacePrimitives(List<String> args){
        args.replaceAll(s -> {
            if (s.equals("int"))
                return Integer.class.getName();
            if (s.equals("long"))
                return Long.class.getName();
            if (s.equals("double"))
                return Double.class.getName();
            if (s.equals("float"))
                return Float.class.getName();
            if (s.equals("boolean"))
                return Boolean.class.getName();
            if (s.equals("char"))
                return Character.class.getName();
            if (s.equals("byte"))
                return Byte.class.getName();
            if (s.equals("short"))
                return Short.class.getName();
            return s;
        });
        return args;
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Value> values = new ArrayList<>(data.length);
                for (int i = 0; i < data.length; i++) {
                    boolean isCustomObject = false;
                    for (String objName : Main.runtimeView.getObjList().keySet())
                        if (objName.equals(data[i][1])) {
                            values.add(Main.runtimeView.getObjList().get(objName).getObjectReference());
                            isCustomObject = true;
                        }

                    if (!isCustomObject)
                        values.add(debugger.stringToValue(Class.forName(classes.get(i)), (String) data[i][1]));
                }

                if (objectItem == null) {
                    ObjectReference or = debugger.instantiateClass(className, method, values);
                    Main.runtimeView.addObject(objectName, or);
                }else {
                    debugger.invokeMethod(objectItem.getObjectReference(), method, values);
                }
                ParameterInputView.this.dispose();
            } catch (InterruptedException | InvalidTypeException | ClassNotLoadedException | InvocationException | IncompatibleThreadStateException | ClassNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }
}
