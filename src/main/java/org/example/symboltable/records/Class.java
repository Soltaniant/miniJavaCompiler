package org.example.symboltable.records;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Class extends Element{

    private final Map<String, Method> methods;
    private final Map<String, Variable> globalVars;

    @Setter
    private String parentClassId;

    public Class(String id) {
        super(id, "class");
        methods = new HashMap<>();
        globalVars = new HashMap<>();
    }
}
