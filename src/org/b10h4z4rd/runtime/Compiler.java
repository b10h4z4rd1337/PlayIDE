package org.b10h4z4rd.runtime;

import javax.swing.*;
import javax.tools.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Mathias on 04.05.15.
 */
public class Compiler {

    public static void compile(List<File> input) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjects(input.toArray(new File[input.size()]));
        DiagnosticListener<JavaFileObject> diagnosticListener = diagnostic -> {
            JFrame frame = new JFrame("Compiler ERROR");
            JTextArea area = new JTextArea();
            area.setText(diagnostic.toString());
            area.setForeground(Color.RED);
            area.setTabSize(2);
            area.setEditable(false);
            frame.setContentPane(new JScrollPane(area));
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        };
        javaCompiler.getTask(null, fileManager, diagnosticListener, null, null, files).call();
        fileManager.close();
    }

}
