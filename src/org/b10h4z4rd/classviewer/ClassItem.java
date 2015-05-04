package org.b10h4z4rd.classviewer;

import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.RuntimeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassItem extends JPanel {

    public static final int WIDTH = 100, HEIGHT = 75;
    public static String NEW_INSTANCE;

    private String className;
    private JLabel classNameLabel;

    private JPopupMenu popupMenu;
    private int pressedX, pressedY;

    public ClassItem(String name){
        this.className = name;
        NEW_INSTANCE = "new " + name;
        setLayout(null);
        setSize(WIDTH, HEIGHT);
        classNameLabel = new JLabel(className);
        classNameLabel.setLocation(WIDTH / 2, HEIGHT / 2);
        add(classNameLabel);

        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(NEW_INSTANCE);
        item.addActionListener(popupActionListener);
        popupMenu.add(item);

        addMouseListener(customMouseAdapter);
        addMouseMotionListener(panelMover);
    }

    private void openCoderFrame(){
        new org.b10h4z4rd.codeeditor.CodeEditor(this);
    }

    private ActionListener popupActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEW_INSTANCE)){
                if (Main.runtimeView == null)
                    Main.runtimeView = new RuntimeView();

                Main.runtimeView.addObject(ClassItem.this, "aaa");
            }
        }


    };

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
    };

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
}
