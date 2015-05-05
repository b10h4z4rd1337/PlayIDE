package org.b10h4z4rd.classviewer;

import org.b10h4z4rd.projectmanager.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassView extends JFrame {

    public static final String CREATE_FILE = "Create File", NEW_PROJECT = "New Project", OPEN_PROJECT = "Open Project",
            SAVE_PROJECT = "Save Project", COMPILE_PROJECT = "Compile";
    private JPopupMenu popupMenu;
    private int clickedX, clickedY;

    private JFileChooser fileChooser;
    private Project project;
    private File projectLocation;

    public ClassView(){
        setLayout(null);
        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(CREATE_FILE);
        item.addActionListener(popupActionListener);
        popupMenu.add(item);

        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        fileMenu.add(createJMenuItem(NEW_PROJECT));
        fileMenu.add(createJMenuItem(OPEN_PROJECT));
        fileMenu.add(createJMenuItem(SAVE_PROJECT));

        menu.add(fileMenu);
        menu.add(createJMenuItem(COMPILE_PROJECT));

        setJMenuBar(menu);

        addMouseListener(customMouseAdapter);

        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuItem createJMenuItem(String name){
        JMenuItem res = new JMenuItem(name);
        res.addActionListener(menuActionListener);
        return res;
    }

    private void loadProject(File projectLocation){
        try {
            FileInputStream fis = new FileInputStream(projectLocation);
            project = (Project) new ObjectInputStream(fis).readObject();
            fis.close();
            for (ClassItem ci : project.getClassItemList()) {
                ci.initControls();
                getContentPane().add(ci);
            }
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveProject(){
        try {
            if (!projectLocation.exists())
                projectLocation.createNewFile();
            FileOutputStream fos = new FileOutputStream(projectLocation);
            new ObjectOutputStream(fos).writeObject(project);
            fos.flush();
            fos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private ActionListener menuActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEW_PROJECT)){
                fileChooser = new JFileChooser();
                fileChooser.showDialog(ClassView.this, "Create");
                if (fileChooser.getSelectedFile() != null) {
                    project = new Project(fileChooser.getSelectedFile().getName());
                    projectLocation = new File(fileChooser.getSelectedFile().getParent(), fileChooser.getSelectedFile().getName() + ".playproject");
                    saveProject();
                    setTitle(fileChooser.getSelectedFile().getName() + ".playproject");
                }
            }else if (e.getActionCommand().equals(OPEN_PROJECT)){
                fileChooser = new JFileChooser();
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().contains(".playproject");
                    }

                    @Override
                    public String getDescription() {
                        return "PlayIDE Projects (*.playprojects)";
                    }
                });
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.showDialog(ClassView.this, "Open");
                if (fileChooser.getSelectedFile() != null) {
                    loadProject(fileChooser.getSelectedFile());
                    projectLocation = fileChooser.getSelectedFile();
                    setTitle(fileChooser.getSelectedFile().getName());
                }
            }else if (e.getActionCommand().equals(SAVE_PROJECT)){
                saveProject();
            }else if (e.getActionCommand().equals(COMPILE_PROJECT)){
                compile();
            }
        }
    };

    public void compile(){
        List<File> toCompile = new ArrayList<File>();
        for (ClassItem ci : project.getClassItemList())
            toCompile.add(ci.getJavaFile());
        try {
            org.b10h4z4rd.Compiler.compile(toCompile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ActionListener popupActionListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(CREATE_FILE)) {
                String name = JOptionPane.showInputDialog(null, "Enter a Class name:");
                if (name != null && !name.isEmpty() && !existsClass(name)) {
                    ClassItem item = new ClassItem(name, projectLocation.getParentFile());
                    item.setLocation(clickedX - ClassItem.WIDTH / 2, clickedY - ClassItem.HEIGHT / 2);
                    project.getClassItemList().add(item);
                    ClassView.this.getContentPane().add(item);
                    saveProject();
                    repaint();
                }
            }
        }
    };

    private boolean existsClass(String name) {
        for (ClassItem ci : project.getClassItemList())
            if (ci.getClassName().equals(name))
                return true;
        return false;
    }

    private MouseAdapter customMouseAdapter = new MouseAdapter(){
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3){
                if (project != null) {
                    clickedX = e.getX();
                    clickedY = e.getY();
                    popupMenu.show(e.getComponent(), clickedX, clickedY);
                }
            }
        }
    };

    @Override
    public void dispose() {
        saveProject();
        super.dispose();
        System.exit(0);
    }
}
