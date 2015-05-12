package org.b10h4z4rd.runtime.view;

import org.b10h4z4rd.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mathias on 05.05.15.
 */
public class TerminalView extends JFrame {

    private JTextArea area;
    private JTextField inputField;

    public TerminalView(){
        area = new JTextArea();
        area.setEditable(false);

        inputField = new JTextField();
        inputField.setSize(400, 50);
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    byte[] data = (inputField.getText() + "\n").getBytes();
                    inputField.setText("");
                    new Thread(() -> {
                        try {
                            Main.debugger.sendInput(data);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }).start();
                }
            }
        });

        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        setSize(400, 400);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        setTitle("Terminal");
    }

    public TerminalOutputStream createNewOutputStream(){
        return new TerminalOutputStream();
    }

    private class TerminalOutputStream extends OutputStream{
        @Override
        public void write(int b) throws IOException {
            area.append(new String(new byte[]{(byte) b}));
        }

        @Override
        public void write(byte[] b) throws IOException {
            area.append(new String(b, 0, b.length));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            area.append(new String(b, off, len));
        }
    }
}
