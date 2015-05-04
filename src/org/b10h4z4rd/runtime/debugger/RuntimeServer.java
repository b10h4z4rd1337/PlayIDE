package org.b10h4z4rd.runtime.debugger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mathias on 02.05.15.
 */
public class RuntimeServer {

    public static final int CREATE_OBJECT = 1, EXECUTE_METHOD = 2, REMOVE_OBJECT = 3;
    public static final String OPERATION = "operation", CLASS_TO_USE = "classToUse",
            METHOD_TO_INVOKE = "methodToInvoke", OBJECT_NAME = "objectName", RETURNVAL = "returnVal";

    public static int operation = -1;
    public static String classToUse, methodToInvoke, objectName;
    public static Object returnVal;
    public static Map<String, Object> nameToObject = new HashMap<String, Object>();
    private static Thread worker, t;

    public static void main(String[] args) {
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (operation != -1){
                        switch (operation){
                            case CREATE_OBJECT:
                                t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            Class<?> c = Class.forName(classToUse);
                                            returnVal = c.newInstance();
                                            nameToObject.put(objectName, returnVal);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                t.setPriority(Thread.MIN_PRIORITY);
                                t.start();
                                break;
                            case EXECUTE_METHOD:
                                t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Object o = nameToObject.get(objectName);
                                            Class<?> c = Class.forName(classToUse);
                                            returnVal = c.getDeclaredMethod(methodToInvoke).invoke(o);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                t.setPriority(Thread.MIN_PRIORITY);
                                t.start();
                                break;
                            case REMOVE_OBJECT:
                                t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            nameToObject.remove(objectName);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                t.setPriority(Thread.MIN_PRIORITY);
                                t.start();
                                break;
                        }
                        operation = -1;
                    }

                    // Give a moment to be able to change Thread state to suspend VM
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "worker");
        worker.setPriority(Thread.MAX_PRIORITY);
        worker.start();
    }
}
