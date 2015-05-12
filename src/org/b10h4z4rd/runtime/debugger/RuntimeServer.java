package org.b10h4z4rd.runtime.debugger;

/**
 * Created by Mathias on 02.05.15.
 */
public class RuntimeServer {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            vmSuspendBreakPoint();
            Thread.sleep(1);
        }
    }

    public static void vmSuspendBreakPoint(){

    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
