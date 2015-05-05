package org.b10h4z4rd.classviewer;

import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.RuntimeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassItem extends JPanel implements Serializable{

    public static final long serialVersionUID = 432658292L;

    public static final int WIDTH = 100, HEIGHT = 75;
    private String NEW_INSTANCE;

    private String className;
    private File javaFile, projectLocation;
    private JLabel classNameLabel;

    private JPopupMenu popupMenu;
    private int pressedX, pressedY;

    public ClassItem(String name, File home){
        this.className = name;
        this.projectLocation = home;
        this.javaFile = new File(home, name + ".java");
        try {
            javaFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initControls();
    }

    public void initControls(){
        NEW_INSTANCE = "new " + className;
        setLayout(null);
        setSize(WIDTH, HEIGHT);
        classNameLabel = new JLabel(className);
        classNameLabel.setLocation(WIDTH / 2, HEIGHT / 2);
        add(classNameLabel);

        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(NEW_INSTANCE);
        item.addActionListener(new PopupActionListener());
        popupMenu.add(item);

        addMouseListener(new CustomMouseAdapter());
        addMouseMotionListener(new PanelMover());
    }

    public void compile(){
        Main.classView.compile();
    }

    private void openCoderFrame(){
        new org.b10h4z4rd.codeeditor.CodeEditor(this);
    }

    private class PopupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEW_INSTANCE)){
                String name = JOptionPane.showInputDialog(null, "Enter an Object name:");

                if (name != null && !name.isEmpty()) {
                    if (Main.runtimeView == null)
                        Main.runtimeView = new RuntimeView(projectLocation);

                    Main.runtimeView.addObject(ClassItem.this, name);
                }
            }
        }
    };

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
    };

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

    public File getJavaFile() {
        return javaFile;
    }
}
