package org.b10h4z4rd.runtime.debugger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

public class JavaProcessBuilder{
    /*-------------------------------------------------[ java-home ]---------------------------------------------------*/

    private File javaHome = new File(System.getProperty("java.home"));

    public JavaProcessBuilder javaHome(File javaHome){
        this.javaHome = javaHome;
        return this;
    }

    public File javaHome(){
        return javaHome;
    }

    /*-------------------------------------------------[ working-dir ]---------------------------------------------------*/
    private File workingDir;

    public JavaProcessBuilder workingDir(String dir){
        return workingDir(new File(dir));
    }

    public JavaProcessBuilder workingDir(File dir){
        workingDir = dir;
        return this;
    }

    public File workingDir(){
        return workingDir;
    }

    /*-------------------------------------------------[ classpath ]---------------------------------------------------*/

    private List<File> classpath = new ArrayList<>();

    public JavaProcessBuilder classpath(String resource){
        return classpath(new File(resource));
    }

    public JavaProcessBuilder classpath(File resource){
        classpath.add(resource);
        return this;
    }

    public List<File> classpath(){
        return classpath;
    }

    /*-------------------------------------------------[ endorsed-dirs ]---------------------------------------------------*/

    private List<File> endorsedDirs = new ArrayList<>();

    public JavaProcessBuilder endorsedDir(String dir){
        return endorsedDir(new File(dir));
    }

    public JavaProcessBuilder endorsedDir(File dir){
        endorsedDirs.add(dir);
        return this;
    }

    public List<File> endorsedDirs(){
        return endorsedDirs;
    }

    /*-------------------------------------------------[ ext-dirs ]---------------------------------------------------*/

    private List<File> extDirs = new ArrayList<>();

    public JavaProcessBuilder extDir(String dir){
        return extDir(new File(dir));
    }

    public JavaProcessBuilder extDir(File dir){
        extDirs.add(dir);
        return this;
    }

    public List<File> extDirs(){
        return extDirs;
    }

    /*-------------------------------------------------[ library-path ]---------------------------------------------------*/

    private List<File> libraryPath = new ArrayList<>();

    public JavaProcessBuilder libraryPath(String dir){
        return libraryPath(new File(dir));
    }

    public JavaProcessBuilder libraryPath(File dir){
        libraryPath.add(dir);
        return this;
    }

    public List<File> libraryPath(){
        return libraryPath;
    }

    /*-------------------------------------------------[ boot-classpath ]---------------------------------------------------*/

    private List<File> bootClasspath = new ArrayList<>();

    public JavaProcessBuilder bootClasspath(String resource){
        return bootClasspath(new File(resource));
    }

    public JavaProcessBuilder bootClasspath(File resource){
        bootClasspath.add(resource);
        return this;
    }

    public List<File> bootClasspath(){
        return bootClasspath;
    }

    /*-------------------------------------------------[ append-boot-classpath ]---------------------------------------------------*/

    private List<File> appendBootClasspath = new ArrayList<>();

    public JavaProcessBuilder appendBootClasspath(String resource){
        return appendBootClasspath(new File(resource));
    }

    public JavaProcessBuilder appendBootClasspath(File resource){
        appendBootClasspath.add(resource);
        return this;
    }

    public List<File> appendBootClasspath(){
        return appendBootClasspath;
    }

    /*-------------------------------------------------[ prepend-boot-classpath ]---------------------------------------------------*/

    private List<File> prependBootClasspath = new ArrayList<>();

    public JavaProcessBuilder prependBootClasspath(String resource){
        return prependBootClasspath(new File(resource));
    }

    public JavaProcessBuilder prependBootClasspath(File resource){
        prependBootClasspath.add(resource);
        return this;
    }

    public List<File> prependBootClasspath(){
        return prependBootClasspath;
    }

    /*-------------------------------------------------[ system-properties ]---------------------------------------------------*/

    private Map<String, String> systemProperties = new HashMap<>();

    public JavaProcessBuilder systemProperty(String name, String value){
        systemProperties.put(name, value);
        return this;
    }

    public JavaProcessBuilder systemProperty(String name){
        return systemProperty(name, null);
    }

    public Map<String, String> systemProperties(){
        return systemProperties;
    }

    /*-------------------------------------------------[ initial-heap ]---------------------------------------------------*/

    private String initialHeap;

    public JavaProcessBuilder initialHeap(int mb){
        return initialHeap(mb+"m");
    }

    public JavaProcessBuilder initialHeap(String size){
        initialHeap = size;
        return this;
    }

    public String initialHeap(){
        return initialHeap;
    }

    /*-------------------------------------------------[ max-heap ]---------------------------------------------------*/

    private String maxHeap;

    public JavaProcessBuilder maxHeap(int mb){
        return maxHeap(mb+"m");
    }

    public JavaProcessBuilder maxHeap(String size){
        maxHeap = size;
        return this;
    }

    public String maxHeap(){
        return maxHeap;
    }

    /*-------------------------------------------------[ vm-type ]---------------------------------------------------*/

    private String vmType;

    public JavaProcessBuilder client(){
        vmType = "-client";
        return this;
    }

    public JavaProcessBuilder server(){
        vmType = "-server";
        return this;
    }

    public String vmType(){
        return vmType;
    }

    /*-------------------------------------------------[ jvm-args ]---------------------------------------------------*/

    private List<String> jvmArgs = new ArrayList<>();

    public JavaProcessBuilder jvmArg(String arg){
        jvmArgs.add(arg);
        return this;
    }

    public List<String> jvmArgs(){
        return jvmArgs;
    }

    /*-------------------------------------------------[ main-class ]---------------------------------------------------*/

    private String mainClass;

    public JavaProcessBuilder mainClass(String mainClass){
        this.mainClass = mainClass;
        return this;
    }

    public String mainClass(){
        return mainClass;
    }

    /*-------------------------------------------------[ debug ]---------------------------------------------------*/

    private boolean debugSuspend;

    public JavaProcessBuilder debugSuspend(boolean suspend){
        this.debugSuspend = suspend;
        return this;
    }

    public boolean debugSuspend(){
        return debugSuspend;
    }

    private int debugPort = -1;

    public JavaProcessBuilder debugPort(int port){
        this.debugPort = port;
        return this;
    }

    public int debugPort(){
        return debugPort;
    }

    /*-------------------------------------------------[ arguments ]---------------------------------------------------*/

    private List<String> args = new ArrayList<>();

    public JavaProcessBuilder arg(String arg){
        args.add(arg);
        return this;
    }

    public List<String> args(){
        return args;
    }

    /*-------------------------------------------------[ Command ]---------------------------------------------------*/

    private static String toString(File fromDir, Iterable<File> files) throws IOException{
        StringBuilder buff = new StringBuilder();
        for(File file: files){
            if(buff.length()>0)
                buff.append(File.pathSeparator);
            if(fromDir==null)
                buff.append(file.getCanonicalPath());
            else
                buff.append(getRelativePath(fromDir, file));
        }
        return buff.toString();
    }

    private static int getHeight(File fromElem, File toElem){
        if(fromElem==null)
            return -1;

        int ht = 0;
        while(fromElem!=toElem){
            fromElem = fromElem.getParentFile();
            ht++;
        }
        return ht;
    }

    private static File getSharedAncestor(File elem1, File elem2){
        if(elem1==elem2)
            return elem1;
        if(elem1==null || elem2==null)
            return null;

        int ht1 = getHeight(elem1, null);
        int ht2 = getHeight(elem2, null);

        int diff;
        if(ht1>ht2)
            diff = ht1 - ht2;
        else{
            diff = ht2 - ht1;
            File temp = elem1;
            elem1 = elem2;
            elem2 = temp;
        }

        // Go up the tree until the nodes are at the same level
        while(diff>0){
            elem1 = elem1.getParentFile();
            diff--;
        }

        // Move up the tree until we find a common ancestor.  Since we know
        // that both nodes are at the same level, we won't cross paths
        // unknowingly (if there is a common ancestor, both nodes hit it in
        // the same iteration).
        do{
            if(elem1.equals(elem2))
                return elem1;
            elem1 = elem1.getParentFile();
            elem2 = elem2.getParentFile();
        }while(elem1 != null); // only need to check one -- they're at the
        // same level so if one is null, the other is

        return null;
    }

    private static String getRelativePath(File fromElem, File toElem){
        if(fromElem==toElem)
            return ".";

        File sharedAncestor = getSharedAncestor(fromElem, toElem);
        if(sharedAncestor==null)
            return null;

        StringBuilder buff1 = new StringBuilder();
        while(!fromElem.equals(sharedAncestor)){
            if(buff1.length()>0)
                buff1.append(File.separator);
            buff1.append("..");
            fromElem = fromElem.getParentFile();
        }

        StringBuilder buff2 = new StringBuilder();
        while(!toElem.equals(sharedAncestor)){
            if(buff2.length()>0)
                buff2.insert(0, File.separator);

            buff2.insert(0, toElem.getName());

            toElem = toElem.getParentFile();
        }

        if(buff1.length()>0 && buff2.length()>0)
            return buff1+File.separator+buff2;
        else
            return buff1.length()>0 ? buff1.toString() : buff2.toString();
    }

    /** Returns command with all its arguments */
    public String[] command() throws IOException{
        List<String> cmd = new ArrayList<>();

        String executable = javaHome.getCanonicalPath()+File.separator+"bin"+File.separator+"java";
        if(System.getProperty("os.name").toLowerCase().contains("windows"))
            executable += ".exe";
        cmd.add(executable);

        String path = toString(workingDir, prependBootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath/p:"+path);

        path = toString(workingDir, bootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath:"+path);

        path = toString(workingDir, appendBootClasspath);
        if(path.length()>0)
            cmd.add("-Xbootclasspath/a:"+path);

        path = toString(workingDir, classpath);
        if(path.length()>0){
            cmd.add("-classpath");
            cmd.add(path);
        }

        path = toString(workingDir, extDirs);
        if(path.length()>0)
            cmd.add("-Djava.ext.dirs="+path);

        path = toString(workingDir, endorsedDirs);
        if(path.length()>0)
            cmd.add("-Djava.endorsed.dirs="+path);

        path = toString(workingDir, libraryPath);
        if(path.length()>0)
            cmd.add("-Djava.library.path="+path);

        for(Map.Entry<String, String> prop: systemProperties.entrySet()){
            if(prop.getValue()==null)
                cmd.add("-D"+prop.getKey());
            else
                cmd.add("-D"+prop.getKey()+"="+prop.getValue());
        }

        if(initialHeap!=null)
            cmd.add("-Xms"+initialHeap);
        if(maxHeap!=null)
            cmd.add("-Xmx"+maxHeap);
        if(vmType!=null)
            cmd.add(vmType);
        if(debugPort!=-1){
            cmd.add("-Xdebug");
            cmd.add("-Xnoagent");
            cmd.add("-Xrunjdwp:transport=dt_socket,server=y,suspend="+(debugSuspend?'y':'n')+",address="+debugPort);
        }
        cmd.addAll(jvmArgs);
        if(mainClass!=null){
            cmd.add(mainClass);
            cmd.addAll(args);
        }

        return cmd.toArray(new String[cmd.size()]);
    }

    public Process launch() throws IOException {
        return Runtime.getRuntime().exec(command(), null, workingDir);
    }

    public static void redirectStreams(Process process, OutputStream output, OutputStream error){
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int len;
            InputStream is = process.getInputStream();
            try {
                len = is.read(buffer);
                while ((len = is.read(buffer)) != -1)
                    output.write(buffer, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            byte[] buffer = new byte[1024];
            int len;
            InputStream is = process.getErrorStream();
            try {
                while ((len = is.read(buffer)) != -1)
                    error.write(buffer, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        outputStream = process.getOutputStream();
    }

    public static OutputStream outputStream;
}
