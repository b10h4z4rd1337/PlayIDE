package org.b10h4z4rd.runtime;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<String> getMethodNames(List<Method> methods){
        List<String> result = new ArrayList<>(methods.size());
        for (Method m : methods)
            result.add(m.name());
        return result;
    }

    public static <T,E> Map<T, E> makeMap(List<T> map1, List<E> map2){
        Map<T, E> result = new HashMap<T, E>(map1.size());
        for (int i = 0; i < map1.size(); i++)
            result.put(map1.get(i), map2.get(i));

        return result;
    }

}
