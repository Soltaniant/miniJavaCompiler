package org.example.symboltable.elements;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Scope extends Element {

    @Setter
    private Scope parent;
    private final Map<String, Element> records;
    private final Class scopeClass;

    public Scope(String id, String type) {
        super(id, type);
        this.parent = null;
        this.scopeClass = null;
        records = new HashMap<>();
    }

    public void addElement(String key, Element record) {
        records.put(key, record);
    }
}
