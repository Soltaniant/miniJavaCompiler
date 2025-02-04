package org.example.symboltable.records;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
public class Method extends Element {
    private final Map<String, Variable> parameters;
    private final Map<String, Variable> localVars;
    private final List<String> paramTypes;

    public Method(String id, String type) {
        super(id, type);
        parameters = new HashMap<>();
        localVars = new HashMap<>();
        paramTypes = new ArrayList<>();
    }

    /**
     * Adds a parameter variable to the method's parameter list
     * @param key The variable's identifier
     * @param newParam The new parameter
     */
    public void pushParameter(String key, Variable newParam) {
        parameters.put(key, newParam);
        // Adds the type of the parameter in the correct order
        if (newParam.getType().equals("int") || newParam.getType().equals("int[]") || newParam.getType().equals("boolean")) {
            paramTypes.add(newParam.getType());
        }
        else {
            paramTypes.add(newParam.getType());
        }
    }

    /**
     * Add a local variable to the method's local variable map
     * @param key The variable's identifier
     * @param newLocalVar The new local variable
     */
    public void pushLocalVar(String key, Variable newLocalVar) {
        localVars.put(key, newLocalVar);
    }
}
