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

    public void addParameter(String key, Variable newParam) {
        parameters.put(key, newParam);
        paramTypes.add(newParam.getType());
    }

    public void addLocalVariable(String key, Variable newLocalVar) {
        localVars.put(key, newLocalVar);
    }
}
