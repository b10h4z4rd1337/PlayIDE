package org.b10h4z4rd.codeeditor;

import org.b10h4z4rd.classviewer.ClassItem;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 01.05.15.
 */
public class CodeEditor extends JFrame implements ActionListener{

    private JTextPane editor;
    private ClassItem classItem;

    public CodeEditor(ClassItem classItem){
        this.classItem = classItem;
        this.editor = new JTextPane();
        initCodeArea();

        // Set Tab Size
        FontMetrics fm = editor.getFontMetrics( editor.getFont() );
        int charWidth = fm.charWidth( 'w' );
        int tabWidth = charWidth * 4;

        TabStop[] tabs = new TabStop[10];

        for (int j = 0; j < tabs.length; j++)
        {
            int tab = j + 1;
            tabs[j] = new TabStop( tab * tabWidth );
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = editor.getDocument().getLength();
        editor.getStyledDocument().setParagraphAttributes(0, length, attributes, true);

        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem compileItem = new JMenuItem("Compile");
        JMenuItem saveItem = new JMenuItem("Save");

        compileItem.addActionListener(this);
        saveItem.addActionListener(this);

        fileMenu.add(saveItem);

        menu.add(fileMenu);
        menu.add(compileItem);

        load();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        add(menu, BorderLayout.NORTH);
        add(new JScrollPane(editor), BorderLayout.CENTER);
        setVisible(true);
        setTitle(classItem.getJavaFile().getName());
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
            classItem.compile();
        }
    }

    @Override
    public void dispose() {
        save();
        super.dispose();
    }

    private void save(){
        try {
            FileOutputStream fos = new FileOutputStream(classItem.getJavaFile());
            byte[] data = editor.getText().getBytes();
            fos.write(data);
            fos.flush();
            fos.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Failed to save file!", "An error occurred!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void load(){
        try {
            FileInputStream fis = new FileInputStream(classItem.getJavaFile());
            StringBuilder sb = new StringBuilder();

            byte[] buffer = new byte[1024 * 10];
            int len;
            while ((len = fis.read(buffer)) != -1)
                sb.append(new String(buffer, 0, len));

            editor.setText(sb.toString());
            fis.close();
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to open file!", "An error occurred!", JOptionPane.ERROR_MESSAGE);
        }
    }
}
