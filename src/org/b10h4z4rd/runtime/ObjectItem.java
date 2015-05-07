package org.b10h4z4rd.runtime;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Created by Mathias on 03.05.15.
 */
public class ObjectItem extends JPanel{

    public static final int WIDTH = 100, HEIGHT = 75;

    private String objectName;
    private JLabel objectNameLabel;
    private JPopupMenu popupMenu;
    private int pressedX, pressedY;

    private class PanelMover extends MouseMotionAdapter{
        @Override
        public void mouseDragged(MouseEvent e) {
            e.translatePoint(e.getComponent().getLocation().x - pressedX, e.getComponent().getLocation().y - pressedY);

            Point newLoca = new Point();
            int PWIDTH = getParent().getWidth(), PHEIGTH = getParent().getHeight();
            if (e.getX() >= 0 && e.getX() <= PWIDTH - WIDTH)
                newLoca.x = e.getX();
            else {
                if (e.getX() > PWIDTH - WIDTH)
                    newLoca.x = PWIDTH - WIDTH;
                else
                    newLoca.x = 0;
            }

            if (e.getY() >= 0 && e.getY() <= PHEIGTH - HEIGHT)
                newLoca.y = e.getY();
            else {
                if (e.getY() > PHEIGTH - HEIGHT)
                    newLoca.y = PHEIGTH - HEIGHT;
                else
                    newLoca.y = 0;
            }

            setLocation(newLoca);
            getParent().repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
        }
    }
    private class CustomMouseAdapter extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                // Possible Object Card
            }else if (e.getButton() == MouseEvent.BUTTON3){
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1){
                pressedX = e.getX();
                pressedY = e.getY();
            }
        }
    }
    private class PopupActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Remove")){
                Main.runtimeView.removeObject(ObjectItem.this);
            }else {
                try {
                    //Invoke Method
                    Method m = methodList.get(e.getActionCommand());
                    if (HelperUtils.needsArguments(m))
                        new ParameterInputView(ObjectItem.this, m);
                    else
                        Main.runtimeView.getDebugger().invokeMethod(objectReference, methodList.get(e.getActionCommand()), new ArrayList<>());
                } catch (InvocationException | InvalidTypeException | IncompatibleThreadStateException | ClassNotLoadedException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private Map<String, Method> methodList;
    private ObjectReference objectReference;

    public ObjectItem(String className, String objectName){
        this.objectName = objectName;
        JVMDebugger debugger = Main.runtimeView.getDebugger();
        try {
            objectReference = debugger.instantiateClass(className, debugger.loadClass(className).methodsByName("<init>").get(0), new ArrayList<>());
        } catch (InvalidTypeException | ClassNotLoadedException | InterruptedException | InvocationException | IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
        init();
    }

    public ObjectItem(String objectName, ObjectReference objectReference){
        this.objectName = objectName;
        this.objectReference = objectReference;
        init();
    }

    private void init(){
        setLayout(null);
        setSize(WIDTH, HEIGHT);
        objectNameLabel = new JLabel(objectName);
        objectNameLabel.setLocation(WIDTH / 2, HEIGHT / 2);
        add(objectNameLabel);

        popupMenu = new JPopupMenu();

        addMouseListener(new CustomMouseAdapter());
        addMouseMotionListener(new PanelMover());

        if (objectReference != null) {
            JMenuItem item;
            java.util.List<Method> methods = objectReference.referenceType().methods();
            methodList = HelperUtils.makeMap(HelperUtils.getMethodNames(methods), methods);
            for (String s : methodList.keySet()) {
                item = new JMenuItem(s);
                item.addActionListener(new PopupActionListener());
                popupMenu.add(item);
            }

            popupMenu.add(createJMenuItem("Remove"));
        }else
            System.out.println("NULL");
    }

    private JMenuItem createJMenuItem(String name){
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(new PopupActionListener());
        return menuItem;
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.RED);
        g.fillRect(2, 2, WIDTH - 4, HEIGHT - 4);
        g.setColor(Color.BLACK);
        g.drawString(objectName, WIDTH / 2 - objectNameLabel.getPreferredSize().width / 2, HEIGHT / 2 - objectNameLabel.getPreferredSize().height / 2);
    }

    public ObjectReference getObjectReference(){
        return objectReference;
    }

    public String getObjectName() {
        return objectName;
    }
}
