package org.b10h4z4rd.codeeditor;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 14.05.15.
 */
public class CodeDocumentV2 extends DefaultStyledDocument {

    private SimpleAttributeSet normal = new SimpleAttributeSet();
    private SimpleAttributeSet comments = new SimpleAttributeSet();
    private SimpleAttributeSet string = new SimpleAttributeSet();
    private SimpleAttributeSet number = new SimpleAttributeSet();

    private char[] text;

    private Map<String, SimpleAttributeSet> keywordSets = new HashMap<>();
    private JTextPane parent;
    private int pos;

    public CodeDocumentV2(JTextPane parent){
        initStyles();
        this.parent = parent;
    }

    private void initStyles() {
        StyleConstants.setForeground(comments, Color.green);
        StyleConstants.setItalic(comments, true);
        StyleConstants.setForeground(string, Color.blue);
        StyleConstants.setForeground(number, Color.red);
    }

    private void analyze() {
        try {
            text = parent.getText().toCharArray();
            int caretPos = parent.getCaretPosition();
            super.remove(0, text.length);

            for (pos = 0; pos < text.length; pos++) {
                char token = text[pos];
                if (token == '/' && text[pos + 1] == '/') {
                    parseComment();
                } else if (Character.isLetter(token)) {
                    parseWord();
                }else if (Character.isDigit(token)){
                    parseNumber();
                }else if (token == '\"' || token == '\''){
                    parseString();
                }else {
                    super.insertString(pos, String.valueOf(token), normal);
                }
            }

            parent.setCaretPosition(caretPos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseComment() throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = pos; i < text.length; i++) {
            sb.append(text[i]);
            if (text[i] == '\n') {
                super.insertString(pos, sb.toString(), comments);
                pos = i;
                return;
            }
        }
    }

    private void parseWord() throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = pos; i < text.length; i++) {
            if (Character.isLetter(text[i])) {
                sb.append(text[i]);
            }else {
                if (keywordSets.containsKey(sb.toString())) {
                    super.insertString(pos, sb.toString(), keywordSets.get(sb.toString()));
                }else {
                    super.insertString(pos, sb.toString(), normal);
                }
                pos = i - 1;
                return;
            }
        }
    }

    private void parseNumber() throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = pos; i < text.length; i++) {
            if (Character.isDigit(text[i])) {
                sb.append(text[i]);
            }else {
                super.insertString(pos, sb.toString(), number);
                pos = i - 1;
                return;
            }
        }
    }

    private void parseString() throws BadLocationException {
        StringBuilder sb = new StringBuilder();
        int i;
        boolean start = false;
        for (i = pos; i < text.length; i++) {
            sb.append(text[i]);
            if (text[i] == '\"' || text[i] == '\'') {
                if (!start)
                    start = true;
                else
                    break;
            }
        }
        super.insertString(pos, sb.toString(), string);
        pos = i;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offs, str, a);
        analyze();
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        analyze();
    }

    public void setKeywords(Map<String, Color> aKeywordList) {
        if (aKeywordList != null) {
            for (Map.Entry<String, Color> entry : aKeywordList.entrySet()) {
                SimpleAttributeSet temp = new SimpleAttributeSet();
                StyleConstants.setForeground(temp, entry.getValue());
                StyleConstants.setBold(temp, true);
                this.keywordSets.put(entry.getKey(), temp);
            }
        }
    }
}
