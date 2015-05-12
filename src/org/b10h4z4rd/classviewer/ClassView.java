package org.b10h4z4rd.classviewer;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import org.b10h4z4rd.Main;
import org.b10h4z4rd.runtime.debugger.JVMDebugger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mathias on 01.05.15.
 */
public class ClassView extends JFrame {

    public static final String CREATE_FILE = "Create File", NEW_PROJECT = "New Project", OPEN_PROJECT = "Open Project",
            SAVE_PROJECT = "Save Project", COMPILE_PROJECT = "Compile";
    private JPopupMenu popupMenu;
    private int clickedX, clickedY;

    private Project project;
    private File projectLocation;

    public ClassView(){
        setLayout(null);
        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem(CREATE_FILE);
        item.addActionListener(new PopupActionListener());
        popupMenu.add(item);

        JMenuBar menu = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        fileMenu.add(createJMenuItem(NEW_PROJECT));
        fileMenu.add(createJMenuItem(OPEN_PROJECT));
        fileMenu.add(createJMenuItem(SAVE_PROJECT));

        menu.add(fileMenu);
        menu.add(createJMenuItem(COMPILE_PROJECT));

        setJMenuBar(menu);

        addMouseListener(new CustomMouseAdapter());

        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JMenuItem createJMenuItem(String name){
        JMenuItem res = new JMenuItem(name);
        res.addActionListener(new MenuActionListener());
        return res;
    }

    private void loadProject(File projectLocation){
        try {
            FileInputStream fis = new FileInputStream(projectLocation);
            project = (Project) new ObjectInputStream(fis).readObject();
            fis.close();
            for (ClassItem classItem : project.getClassItemList()){
                classItem.initControls();
                add(classItem);
            }
            loadClasses();
            repaint();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        for (ClassItem classItem : project.getClassItemList())
            if (new File(classItem.getJavaFile().getParentFile(), classItem.getClassName() + ".class").exists())
                classItem.reloadConstructors();
    }

    public File getProjectFolder(){
        return projectLocation.getParentFile();
    }

    private void saveProject(){
        if (project != null) {
            try {
                if (!projectLocation.exists())
                    if (!projectLocation.createNewFile())
                        JOptionPane.showMessageDialog(this, "Project could not be created!", "ERROR", JOptionPane.ERROR_MESSAGE);
                FileOutputStream fos = new FileOutputStream(projectLocation);
                new ObjectOutputStream(fos).writeObject(project);
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class MenuActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (e.getActionCommand().equals(NEW_PROJECT)){
                fileChooser.showDialog(ClassView.this, "Create");
                if (fileChooser.getSelectedFile() != null) {
                    projectLocation = new File(fileChooser.getSelectedFile().getParent(), fileChooser.getSelectedFile().getName() + ".playproject");
                    project = new Project();
                    initVM();
                    saveProject();
                    setTitle(fileChooser.getSelectedFile().getName() + ".playproject");
                }
            }else if (e.getActionCommand().equals(OPEN_PROJECT)){
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase().contains(".playproject") || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return "PlayIDE Projects (*.playprojects)";
                    }
                });
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.showDialog(ClassView.this, "Open");
                if (fileChooser.getSelectedFile() != null) {
                    projectLocation = fileChooser.getSelectedFile();
                    initVM();
                    loadProject(fileChooser.getSelectedFile());
                    setTitle(fileChooser.getSelectedFile().getName());
                }
            }else if (e.getActionCommand().equals(SAVE_PROJECT)){
                saveProject();
            }else if (e.getActionCommand().equals(COMPILE_PROJECT)){
                compile();
            }
        }
    }

    public void initVM(){
        if (Main.debugger == null) {
            try {
                Main.debugger = new JVMDebugger(getProjectFolder(), 7000);
                if (project != null)
                    loadClasses();
            } catch (IOException | JVMDebugger.ConnectorNotFoundException | InterruptedException | IllegalConnectorArgumentsException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void compile(){
        if (project != null) {
            List<File> toCompile = project.getClassItemList().stream().map(ClassItem::getJavaFile).collect(Collectors.toList());
            try {
                org.b10h4z4rd.runtime.Compiler.compile(toCompile);
                Main.debugger.restart();
            } catch (IOException | InvalidTypeException | IncompatibleThreadStateException | ClassNotLoadedException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeItem(ClassItem classItem){
        project.getClassItemList().remove(classItem);
        remove(classItem);
        saveProject();
        repaint();
    }

    private class PopupActionListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(CREATE_FILE)) {
                String name = JOptionPane.showInputDialog(null, "Enter a Class name:");
                if (name != null && !name.isEmpty() && !existsClass(name)) {
                    ClassItem item = new ClassItem(name);
                    item.setLocation(clickedX - ClassItem.WIDTH / 2, clickedY - ClassItem.HEIGHT / 2);
                    project.getClassItemList().add(item);
                    ClassView.this.getContentPane().add(item);
                    saveProject();
                    repaint();
                }
            }
        }
    }

    private boolean existsClass(String name) {
        for (ClassItem ci : project.getClassItemList())
            if (ci.getClassName().equals(name))
                return true;
        return false;
    }

    private class CustomMouseAdapter extends MouseAdapter{
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
    }

    @Override
    public void dispose() {
        saveProject();
        super.dispose();
        if (Main.debugger != null) {
            try {
                Main.debugger.exit();
            } catch (ClassNotLoadedException | IncompatibleThreadStateException | InvalidTypeException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
