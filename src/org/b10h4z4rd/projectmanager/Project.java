package org.b10h4z4rd.projectmanager;

import org.b10h4z4rd.classviewer.ClassItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 05.05.15.
 */
public class Project implements Serializable {

    public static final long serialVersionUID = 4832632847L;

    private String projectName;
    private List<ClassItem> classItemList;

    public Project(String name){
        this.projectName = name;
        this.classItemList = new ArrayList<ClassItem>();
    }

    public String getProjectName() {
        return projectName;
    }

    public List<ClassItem> getClassItemList() {
        return classItemList;
    }
}
