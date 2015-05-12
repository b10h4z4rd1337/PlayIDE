package org.b10h4z4rd.runtime.view;

import com.sun.jdi.Value;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mathias on 07.05.15.
 */
public class ReturnView extends JFrame {

    public ReturnView(Value value){
        if (value.equals(JVMDebugger.VOID))
            return;
        String[] splitted = value.type().name().split("\\.");
        String type = splitted[splitted.length - 1];
        JLabel valueLabel = new JLabel(type + ": " + value.toString());
        valueLabel.setFont(new Font(valueLabel.getFont().getName(), Font.PLAIN, 20));
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(valueLabel, new GridBagConstraints());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setPreferredSize(new Dimension(valueLabel.getPreferredSize().width + 10, valueLabel.getPreferredSize().height + 10));
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("Return");
        setVisible(true);
    }
}
