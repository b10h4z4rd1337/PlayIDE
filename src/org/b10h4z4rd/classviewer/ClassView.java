package org.b10h4z4rd.classviewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassView extends JPanel {

    public static String CREATE_FILE = "Create File";
    private List<ClassItem> classList = new ArrayList<ClassItem>();
    private JPopupMenu popupMenu;
    private int clickedX, clickedY;

    public ClassView(){
        setLayout(null);
        setBackground(Color.WHITE);

        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(CREATE_FILE);
        item.addActionListener(new PopupActionListener());
        popupMenu.add(item);

        addMouseListener(new CustomMouseAdapter());
    }

    private class PopupActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(CREATE_FILE)){
                ClassItem item = new ClassItem("HelloWorld");
                item.setLocation(clickedX - ClassItem.WIDTH / 2, clickedY - ClassItem.HEIGHT / 2);
                classList.add(item);
                ClassView.this.add(item);
                repaint();
            }
        }
    }

    private class CustomMouseAdapter extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3){
                clickedX = e.getX();
                clickedY = e.getY();
                popupMenu.show(e.getComponent(), clickedX, clickedY);
            }
        }
    }
}
