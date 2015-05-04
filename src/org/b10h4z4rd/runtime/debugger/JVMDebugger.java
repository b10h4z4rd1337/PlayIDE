package org.b10h4z4rd.runtime.debugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import jlibs.core.lang.JavaProcessBuilder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Mathias on 02.05.15.
 */
public class JVMDebugger {

    private VirtualMachine vm;
    private ThreadReference workerThread;
    public static ClassType SERVER_CLASS;
    private JavaProcessBuilder javaProcessBuilder;
    private Process javaProcess;
    private int port;

    public JVMDebugger(File workingDir, int port){
        this.port = port;
        javaProcessBuilder = new JavaProcessBuilder();
        javaProcessBuilder.workingDir(workingDir);
        javaProcessBuilder.mainClass(RuntimeServer.class.getName());
        javaProcessBuilder.debugPort(7000);
    }

    public void launch(OutputStream output, OutputStream error) throws IOException, InterruptedException, ConnectorNotFoundException, IllegalConnectorArgumentsException {
        javaProcess = javaProcessBuilder.launch(output, error);

        // Give the new JVM time to set up before we attach!
        Thread.sleep(100);

        VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
        AttachingConnector connector = null;
        for (AttachingConnector attachingConnector : virtualMachineManager.attachingConnectors()){
            if (attachingConnector.transport().name().equalsIgnoreCase("dt_socket")){
                connector = attachingConnector;
            }
        }
        if (connector == null)
            throw new ConnectorNotFoundException();

        Map<String, Connector.Argument> params = connector.defaultArguments();
        params.get("hostname").setValue("127.0.0.1");
        params.get("port").setValue(String.valueOf(port));
        vm = connector.attach(params);
        SERVER_CLASS = (ClassType) vm.classesByName(RuntimeServer.class.getName()).get(0);
    }

    private void setStaticField(ClassType classType, String fieldName, Value value) throws ClassNotLoadedException, InvalidTypeException {
        classType.setValue(classType.fieldByName(fieldName), value);
    }

    private Value getStaticField(ClassType classType, String fieldName){
        return classType.getValue(classType.fieldByName(fieldName));
    }

    public ObjectReference instantiateClass(String className, String objectName) throws InvalidTypeException, ClassNotLoadedException, InterruptedException {
        vm.suspend();
        setStaticField(SERVER_CLASS, RuntimeServer.OPERATION, vm.mirrorOf(RuntimeServer.CREATE_OBJECT));
        setStaticField(SERVER_CLASS, RuntimeServer.CLASS_TO_USE, vm.mirrorOf(className));
        setStaticField(SERVER_CLASS, RuntimeServer.OBJECT_NAME, vm.mirrorOf(objectName));
        vm.resume();

        Thread.sleep(10);

        return (ObjectReference) getReturn();
    }

    public Value invokeMethod(String className, String objectName, String methodName) throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InterruptedException {
        vm.suspend();
        setStaticField(SERVER_CLASS, RuntimeServer.OPERATION, vm.mirrorOf(RuntimeServer.EXECUTE_METHOD));
        setStaticField(SERVER_CLASS, RuntimeServer.CLASS_TO_USE, vm.mirrorOf(className));
        if (objectName == null)
            objectName = "";
        setStaticField(SERVER_CLASS, RuntimeServer.OBJECT_NAME, vm.mirrorOf(objectName));
        setStaticField(SERVER_CLASS, RuntimeServer.METHOD_TO_INVOKE, vm.mirrorOf(methodName));
        vm.resume();

        Thread.sleep(10);

        return getReturn();
    }

    public void removeObject(String name) throws InvalidTypeException, ClassNotLoadedException {
        setStaticField(SERVER_CLASS, RuntimeServer.OPERATION, vm.mirrorOf(RuntimeServer.REMOVE_OBJECT));
    }

    private Value getReturn(){
        vm.suspend();
        Value value = getStaticField(SERVER_CLASS, RuntimeServer.RETURNVAL);
        vm.resume();
        return value;
    }

    public void exit(){
        vm.exit(0);
    }

    public void kill(){
        javaProcess.destroy();
    }

}
