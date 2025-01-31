package org.example.symboltable;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public Map<String, SymbolTableEntry> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public void put(String name, SymbolTableEntry entry) {
        table.put(name, entry);
    }

    public SymbolTableEntry get(String name) {
        return table.get(name);
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }
}
