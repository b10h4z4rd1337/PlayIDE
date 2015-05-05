package org.b10h4z4rd.runtime;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by Mathias on 03.05.15.
 */
public class ObjectItem extends JPanel{

    public static final int WIDTH = 100, HEIGHT = 75;

    private String objectName, className;
    private JLabel objectNameLabel;
    private JPopupMenu popupMenu;
    private int pressedX, pressedY;

    private MouseMotionAdapter panelMover = new MouseMotionAdapter(){
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
    };
    private MouseAdapter customMouseAdapter = new MouseAdapter(){
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
    };
    private ActionListener popupActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Remove")){
                Main.runtimeView.removeObject(ObjectItem.this);
            }else {
                try {

                    //Invoke Method
                    Main.runtimeView.getDebugger().invokeMethod(className, objectName, e.getActionCommand());

                } catch (InvocationException e1) {
                    e1.printStackTrace();
                } catch (InvalidTypeException e1) {
                    e1.printStackTrace();
                } catch (ClassNotLoadedException e1) {
                    e1.printStackTrace();
                } catch (IncompatibleThreadStateException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    };

    private List<Method> methodList;
    private ObjectReference objectReference;

    public ObjectItem(String className, String objectName){
        this.objectName = objectName;
        this.className = className;
        setLayout(null);
        setSize(WIDTH, HEIGHT);
        objectNameLabel = new JLabel(objectName);
        objectNameLabel.setLocation(WIDTH / 2, HEIGHT / 2);
        add(objectNameLabel);

        popupMenu = new JPopupMenu();

        addMouseListener(customMouseAdapter);
        addMouseMotionListener(panelMover);

        try {
            objectReference = Main.runtimeView.getDebugger().instantiateClass(className, objectName);

            if (objectReference != null) {
                JMenuItem item;
                List<Method> methods = objectReference.referenceType().methods();
                for (int i = 1; i < methods.size(); i++){
                    item = new JMenuItem(methods.get(i).name());
                    item.addActionListener(popupActionListener);
                    popupMenu.add(item);
                }

                popupMenu.add(createJMenuItem("Remove"));
            }else
                System.out.println("NULL");

        } catch (InvalidTypeException e) {
            e.printStackTrace();
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private JMenuItem createJMenuItem(String name){
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(popupActionListener);
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

    public String getObjectName() {
        return objectName;
    }

    public void remove(){
        Main.runtimeView.remove(this);
    }
}
