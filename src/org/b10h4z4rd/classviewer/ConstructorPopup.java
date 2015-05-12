package org.b10h4z4rd.classviewer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mathias on 08.05.15.
 */
public class ConstructorPopup extends JPopupMenu {

    public ConstructorPopup(ClassItem classItem, ClassItem.PopupActionListener popupActionListener){
        JMenuItem item;
        for (String s : classItem.getMethodNames()) {
            item = new JMenuItem(s);
            item.addActionListener(popupActionListener);
            add(item);
        }
        item = new JMenuItem("Remove");
        item.setForeground(Color.RED);
        item.addActionListener(popupActionListener);
        add(item);
    }

}
