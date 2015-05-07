package org.b10h4z4rd.classviewer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathias on 05.05.15.
 */
public class Project implements Serializable {

    public static final long serialVersionUID = 4832632847L;
    private List<ClassItem> classItemList;

    public Project(){
        this.classItemList = new ArrayList<>();
    }

    public List<ClassItem> getClassItemList() {
        return classItemList;
    }
}
