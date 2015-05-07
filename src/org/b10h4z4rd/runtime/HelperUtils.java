package org.b10h4z4rd.runtime;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mathias on 06.05.15.
 */
public class HelperUtils {

    public static boolean needsArguments(Method m){
        try {
            return m.argumentTypes().size() > 0;
        } catch (ClassNotLoadedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getSimpleName(String name){
        String[] splitted = name.split("\\.");
        return splitted[splitted.length - 1];
    }

    public static List<Method> getNormalMethods(List<Method> methods) throws ClassNotLoadedException {
        return methods.stream().filter(m -> !m.name().contains("<init>")).collect(Collectors.toList());
    }

    public static List<Method> getConstructors(List<Method> methods){
        return methods.stream().filter(m -> m.name().contains("<init>")).collect(Collectors.toList());
    }

    public static List<String> methodsToString(List<Method> methods){
        List<String> result = new ArrayList<>();
        StringBuilder sb;
        for (Method m : methods) {
            sb = new StringBuilder();
            List<String> argsTypes = m.argumentTypeNames();
            for (int i = 0; i < argsTypes.size(); i++) {
                sb.append(getSimpleName(argsTypes.get(i)));
                if (i + 1 != argsTypes.size())
                    sb.append(", ");
            }
            result.add(getSimpleName(m.returnTypeName()) + " " + m.name() + "(" + sb.toString() + ")");
        }
        return result;
    }

    public static <T,E> Map<T, E> makeMap(List<T> map1, List<E> map2){
        Map<T, E> result = new HashMap<>(map1.size());
        for (int i = 0; i < map1.size(); i++)
            result.put(map1.get(i), map2.get(i));

        return result;
    }

}
