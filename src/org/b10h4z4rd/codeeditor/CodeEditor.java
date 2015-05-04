package org.b10h4z4rd.codeeditor;

import org.b10h4z4rd.classviewer.ClassItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 01.05.15.
 */
public class CodeEditor extends JFrame implements ActionListener{

    private JTextArea editor;
    private ClassItem classItem;
    private File sourceFile;

    public CodeEditor(ClassItem classItem){
        this.classItem = classItem;
        this.editor = new JTextArea();
        this.sourceFile = new File(classItem.getClassName() + ".java");
        JMenuBar menu = new JMenuBar();
        initCodeArea();

        JMenu fileMenu = new JMenu("File");

        JMenuItem compileItem = new JMenuItem("Compile");
        JMenuItem saveItem = new JMenuItem("Save");

        compileItem.addActionListener(this);
        saveItem.addActionListener(this);

        fileMenu.add(saveItem);

        menu.add(fileMenu);
        menu.add(compileItem);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        add(menu, BorderLayout.NORTH);
        add(new JScrollPane(editor), BorderLayout.CENTER);
        setVisible(true);
    }

    private void initCodeArea(){
        CodeDocument doc = new CodeDocument();
        Map<String,Color> keywords = new HashMap<String,Color>();

        Color comment = new Color(63,197,95);
        Color javadoc = new Color(63,95,191);
        Color annotation = new Color(100,100,100);
        doc.setCommentColor(comment);
        doc.setJavadocColor(javadoc);
        doc.setAnnotationColor(annotation);

        Color defColor = new Color(127,0,85);
        keywords.put("abstract",defColor);
        keywords.put("boolean",defColor);
        keywords.put("break",defColor);
        keywords.put("byte",defColor);
        keywords.put("case",defColor);
        keywords.put("catch",defColor);
        keywords.put("char",defColor);
        keywords.put("class",defColor);
        keywords.put("continue",defColor);
        keywords.put("default",defColor);
        keywords.put("do",defColor);
        keywords.put("double",defColor);
        keywords.put("enum",defColor);
        keywords.put("extends",defColor);
        keywords.put("else",defColor);
        keywords.put("false",defColor);
        keywords.put("final",defColor);
        keywords.put("finally",defColor);
        keywords.put("float",defColor);
        keywords.put("for",defColor);
        keywords.put("if",defColor);
        keywords.put("implements",defColor);
        keywords.put("import",defColor);
        keywords.put("instanceof",defColor);
        keywords.put("int",defColor);
        keywords.put("interface",defColor);
        keywords.put("long",defColor);
        keywords.put("native",defColor);
        keywords.put("new",defColor);
        keywords.put("null",defColor);
        keywords.put("package",defColor);
        keywords.put("private",defColor);
        keywords.put("protected",defColor);
        keywords.put("public",defColor);
        keywords.put("return",defColor);
        keywords.put("short",defColor);
        keywords.put("static",defColor);
        keywords.put("super",defColor);
        keywords.put("switch",defColor);
        keywords.put("synchronized",defColor);
        keywords.put("this",defColor);
        keywords.put("throw",defColor);
        keywords.put("throws",defColor);
        keywords.put("transient",defColor);
        keywords.put("true", defColor);
        keywords.put("try", defColor);
        keywords.put("void", defColor);
        keywords.put("volatile", defColor);
        keywords.put("while", defColor);
        doc.setKeywords(keywords);
        editor.setDocument(doc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Save")){
            save();
        }else if (e.getActionCommand().equals("Compile")){
            save();
            try {
                org.b10h4z4rd.Compiler.compile(sourceFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void save(){
        try {
            FileOutputStream fos = new FileOutputStream(sourceFile);
            byte[] data = editor.getText().getBytes();
            fos.write(data);
            fos.flush();
            fos.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Failed to save file!", "An error occrued!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
