package org.b10h4z4rd;

import org.b10h4z4rd.classviewer.ClassView;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;
import org.b10h4z4rd.runtime.view.RuntimeView;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    public static RuntimeView runtimeView = null;
    public static ClassView classView = null;
    public static JVMDebugger debugger = null;
    public static Image icon;
    public static ArrayList<String> keywords;

    public static void main(String[] args) {
        URL url = Main.class.getClassLoader().getResource("Duke_Blocks.png");
        if (url != null) {
            icon = new ImageIcon(url).getImage();
            com.apple.eawt.Application.getApplication().setDockIconImage(icon);
        }
        loadKeywords();
        SwingUtilities.invokeLater(() -> classView = new ClassView());
    }
    
    public static void loadKeywords(){
        keywords = new ArrayList<>();
        keywords.add("abstract");
        keywords.add("boolean");
        keywords.add("break");
        keywords.add("byte");
        keywords.add("case");
        keywords.add("catch");
        keywords.add("char");
        keywords.add("class");
        keywords.add("continue");
        keywords.add("default");
        keywords.add("do");
        keywords.add("double");
        keywords.add("enum");
        keywords.add("extends");
        keywords.add("else");
        keywords.add("false");
        keywords.add("final");
        keywords.add("finally");
        keywords.add("float");
        keywords.add("for");
        keywords.add("if");
        keywords.add("implements");
        keywords.add("import");
        keywords.add("instanceof");
        keywords.add("int");
        keywords.add("interface");
        keywords.add("long");
        keywords.add("native");
        keywords.add("new");
        keywords.add("null");
        keywords.add("package");
        keywords.add("private");
        keywords.add("protected");
        keywords.add("public");
        keywords.add("return");
        keywords.add("short");
        keywords.add("static");
        keywords.add("super");
        keywords.add("switch");
        keywords.add("synchronized");
        keywords.add("this");
        keywords.add("throw");
        keywords.add("throws");
        keywords.add("transient");
        keywords.add("true");
        keywords.add("try");
        keywords.add("void");
        keywords.add("volatile");
        keywords.add("while");
    }
}
