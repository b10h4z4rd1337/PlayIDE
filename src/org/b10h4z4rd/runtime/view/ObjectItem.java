package org.b10h4z4rd.runtime.view;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.HelperUtils;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

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
                        new ReturnView(Main.debugger.invokeMethod(objectReference, methodList.get(e.getActionCommand()), new ArrayList<>()));
                } catch (InvocationException | InvalidTypeException | IncompatibleThreadStateException | ClassNotLoadedException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private Map<String, Method> methodList;
    private ObjectReference objectReference;

    public ObjectItem(String className, Method method, String objectName){
        this.objectName = objectName;
        JVMDebugger debugger = Main.debugger;
        try {
            objectReference = debugger.instantiateClass(className, method, new ArrayList<>());
        } catch (InvalidTypeException | ClassNotLoadedException | InterruptedException e) {
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
            try {
                java.util.List<Method> methods = HelperUtils.getNormalMethods(objectReference.referenceType().methods());
                methodList = HelperUtils.makeMap(HelperUtils.methodsToString(methods), methods);

                for (String s : methodList.keySet()) {
                    item = new JMenuItem(s);
                    item.addActionListener(new PopupActionListener());
                    popupMenu.add(item);
                }

                JMenuItem removeItem = new JMenuItem("Remove");
                removeItem.setForeground(Color.RED);
                removeItem.addActionListener(new PopupActionListener());
                popupMenu.add(removeItem);
            } catch (ClassNotLoadedException e) {
                e.printStackTrace();
            }
        }else
            System.out.println("NULL");
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRoundRect(0, 0, WIDTH, HEIGHT, 20, 20);
        g.setColor(Color.RED);
        g.fillRoundRect(2, 2, WIDTH - 4, HEIGHT - 4, 20, 20);
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
