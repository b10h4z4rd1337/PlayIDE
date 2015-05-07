package org.b10h4z4rd.runtime.view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mathias on 05.05.15.
 */
public class TerminalView extends JFrame {

    private JTextArea area;

    public TerminalView(){
        area = new JTextArea();
        area.setEditable(false);
        setLayout(new BorderLayout());
        add(new JScrollPane(area));

        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setTitle("Terminal");
    }

    public TerminalOutputstream createNewOutputStream(){
        return new TerminalOutputstream();
    }

    private class TerminalOutputstream extends OutputStream{
        @Override
        public void write(int b) throws IOException {
            area.append(new String(new byte[]{(byte) b}));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            area.append(new String(b, off, len));
        }
    }
}
