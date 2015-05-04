package org.b10h4z4rd;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;

/**
 * Created by Mathias on 04.05.15.
 */
public class Compiler {

    public static void compile(File file) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjects(file);
        javaCompiler.getTask(null, fileManager, null, null, null, files).call();
        fileManager.close();
    }

}
