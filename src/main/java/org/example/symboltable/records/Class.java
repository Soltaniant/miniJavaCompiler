package org.example.symboltable.records;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class Class extends Element{

    @Getter
    private Map<String, Method> methods;
    @Getter
    private Map<String, Variable> globalVars;

    @Setter
    private String parentClassId;

    public Class(String id) {
        super(id, "class");
        methods = new HashMap<>();
        globalVars = new HashMap<>();
    }
}
