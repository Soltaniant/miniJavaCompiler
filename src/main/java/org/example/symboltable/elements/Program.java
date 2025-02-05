package org.example.symboltable.elements;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Program extends Element{

    private final Map<String, Class> classes;

    public Program(String id, String type) {
        super(id, type);
        classes = new HashMap<>();
    }

    public void addClass(String key, Class newClass) {
        classes.put(key, newClass);
    }
}
