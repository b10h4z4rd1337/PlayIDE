package org.b10h4z4rd.classviewer;

import com.sun.jdi.*;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.HelperUtils;
import org.b10h4z4rd.runtime.view.RuntimeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassItem extends JPanel{

    public static final int WIDTH = 100, HEIGHT = 75;

    private String className;
    transient private File javaFile;
    transient private JLabel classNameLabel;
    transient Map<String, Method> constructorList;

    transient private JPopupMenu popupMenu;
    transient private PopupActionListener popupActionListener;
    transient private int pressedX, pressedY;

    public ClassItem(String name){
        this.className = name;
        this.javaFile = new File(Main.classView.getProjectFolder(), className + ".java");
        try {
            if (!javaFile.createNewFile())
                JOptionPane.showMessageDialog(null, "Could not create File", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initControls();
    }

    public void initControls(){
        setLayout(null);
        setSize(WIDTH, HEIGHT);
        if (javaFile == null)
            this.javaFile = new File(Main.classView.getProjectFolder(), className + ".java");
        classNameLabel = new JLabel(className);
        classNameLabel.setLocation(WIDTH / 2, HEIGHT / 2);
        add(classNameLabel);

        popupMenu = new JPopupMenu();
        popupActionListener = new PopupActionListener();

        JMenuItem item = new JMenuItem("Remove");
        item.setForeground(Color.RED);
        item.addActionListener(popupActionListener);
        popupMenu.add(item);

        addMouseListener(new CustomMouseAdapter());
        addMouseMotionListener(new PanelMover());
    }

    public void reloadConstructors() {
        try {
            ClassType ct = Main.debugger.loadClass(className);
            java.util.List<Method> constructors = HelperUtils.getConstructors(ct.methods());
            constructorList = HelperUtils.makeMap(HelperUtils.methodsToString(constructors), constructors);
            popupMenu = new JPopupMenu();

            JMenuItem item;
            for (String s : constructorList.keySet()) {
                item = new JMenuItem(s);
                item.addActionListener(popupActionListener);
                popupMenu.add(item);
            }
            item = new JMenuItem("Remove");
            item.setForeground(Color.RED);
            item.addActionListener(popupActionListener);
            popupMenu.add(item);
        } catch (InvocationException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        if (!javaFile.delete()) {
            JOptionPane.showMessageDialog(null, "Failed to remove!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!new File(Main.classView.getProjectFolder(), className + ".class").delete()) {
            JOptionPane.showMessageDialog(null, "Failed to remove!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Main.classView.removeItem(this);
    }

    private void openCoderFrame(){
        new org.b10h4z4rd.codeeditor.CodeEditor(this);
    }

    private class PopupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Remove")){
                delete();
            }else {
                String name = JOptionPane.showInputDialog(null, "Enter an Object name:");

                if (name != null && !name.isEmpty()) {
                    if (Main.runtimeView == null) {
                        Main.runtimeView = new RuntimeView();
                    }
                    try {
                        Main.debugger.loadClass(className);
                        Main.runtimeView.newObject(ClassItem.this, constructorList.get(e.getActionCommand()), name);
                    } catch (InvocationException | InvalidTypeException | ClassNotLoadedException | IncompatibleThreadStateException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

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
                openCoderFrame();
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

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.GREEN);
        g.fillRect(2, 2, WIDTH - 4, HEIGHT - 4);
        g.setColor(Color.BLACK);
        g.drawString(className, WIDTH / 2 - classNameLabel.getPreferredSize().width / 2, HEIGHT / 2 - classNameLabel.getPreferredSize().height / 2);
    }

    public String getClassName() {
        return className;
    }

    public File getJavaFile() {
        return javaFile;
    }
}
