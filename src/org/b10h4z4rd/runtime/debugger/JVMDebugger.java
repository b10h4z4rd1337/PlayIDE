package org.b10h4z4rd.runtime.debugger;

import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.request.BreakpointRequest;
import org.b10h4z4rd.Main;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mathias on 02.05.15.
 */
public class JVMDebugger {

    private VirtualMachine vm;
    private ThreadReference workerThread;
    private BreakpointRequest breakpoint;
    public static ClassType SERVER_CLASS;
    public static Value VOID;
    private Process javaProcess;

    public JVMDebugger(File workingDir, int port) throws IOException, ConnectorNotFoundException, IllegalConnectorArgumentsException, InterruptedException, URISyntaxException {
        JavaProcessBuilder javaProcessBuilder = new JavaProcessBuilder();
        javaProcessBuilder.classpath(workingDir);
        javaProcessBuilder.classpath(new File(findLibHome(), "PlayIDE"));
        javaProcessBuilder.mainClass(RuntimeServer.class.getName());
        javaProcessBuilder.debugPort(port);
        javaProcess = javaProcessBuilder.launch();

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
        params.get("hostname").setValue("localhost");
        params.get("port").setValue(String.valueOf(port));
        vm = connector.attach(params);
        SERVER_CLASS = (ClassType) vm.classesByName(RuntimeServer.class.getName()).get(0);
        breakpoint = vm.eventRequestManager().createBreakpointRequest(SERVER_CLASS.methodsByName("vmSuspendBreakPoint").get(0).location());
        breakpoint.enable();
        workerThread = findMainThread();
        VOID = vm.mirrorOfVoid();
    }

    private File findLibHome() throws URISyntaxException {
        CodeSource codeSource = Main.class.getProtectionDomain().getCodeSource();
        return new File(codeSource.getLocation().toURI().getPath()).getParentFile();
    }

    public void redirectStreams(OutputStream output, OutputStream error){
        JavaProcessBuilder.redirectStreams(javaProcess, output, error);
    }

    public void sendInput(byte[] data) throws IOException {
        JavaProcessBuilder.outputStream.write(data);
        JavaProcessBuilder.outputStream.flush();
    }

    private ThreadReference findMainThread(){
        for (ThreadReference tr : vm.allThreads())
            if (tr.name().equals("main"))
                return tr;
        return null;
    }

    public ClassType loadClass(String className) throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException {
        List<Value> list = new ArrayList<>();
        list.add(vm.mirrorOf(className));
        Value result = SERVER_CLASS.invokeMethod(workerThread, SERVER_CLASS.methodsByName("loadClass").get(0), list, ObjectReference.INVOKE_SINGLE_THREADED);
        return (ClassType) ((ClassObjectReference) result).reflectedType();
    }

    public Value stringToValue(Class<?> clazz, String value){
        if ( Boolean.class == clazz ) return vm.mirrorOf(Boolean.parseBoolean(value));
        if ( Byte.class == clazz ) return vm.mirrorOf(Byte.parseByte(value));
        if ( Character.class == clazz) return vm.mirrorOf(value.charAt(0));
        if ( Short.class == clazz ) return vm.mirrorOf(Short.parseShort(value));
        if ( Integer.class == clazz ) return vm.mirrorOf(Integer.parseInt(value));
        if ( Long.class == clazz ) return vm.mirrorOf(Long.parseLong(value));
        if ( Float.class == clazz ) return vm.mirrorOf(Float.parseFloat(value));
        if ( Double.class == clazz ) return vm.mirrorOf(Double.parseDouble(value));
        return vm.mirrorOf(value);
    }

    public ObjectReference instantiateClass(String className, Method method, List<Value> params) throws InvalidTypeException, ClassNotLoadedException, InterruptedException {
        ClassType ct = (ClassType) vm.classesByName(className).get(0);
        ObjectReference or = null;
        try {
            or = ct.newInstance(workerThread, method, params, ObjectReference.INVOKE_SINGLE_THREADED);
            or.disableCollection();
        } catch (IncompatibleThreadStateException | InvocationException e) {
            e.printStackTrace();
        }

        return or;
    }

    public Value invokeMethod(ObjectReference objectReference, Method method, List<Value> params) throws InvocationException, InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InterruptedException {
        try {
            return objectReference.invokeMethod(workerThread, method, params, ObjectReference.INVOKE_SINGLE_THREADED);
        }catch (InvocationException e){
            System.err.println(e.exception().referenceType().name());
            return null;
        }
    }

    public void removeObject(ObjectReference objectReference) throws InvalidTypeException, ClassNotLoadedException {
        objectReference.enableCollection();
    }

    public void exit() throws ClassNotLoadedException, IncompatibleThreadStateException, InvalidTypeException {
        vm.exit(0);
        javaProcess.destroy();
        Main.debugger = null;
    }

    public void restart() throws ClassNotLoadedException, IncompatibleThreadStateException, InvalidTypeException {
        exit();
        Main.classView.initVM();
    }

    public static class ConnectorNotFoundException extends Exception {}
}
