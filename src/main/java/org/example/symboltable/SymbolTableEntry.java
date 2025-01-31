package org.example.symboltable;

public class SymbolTableEntry {
    private String type;  // Could be a class type, variable type, etc.
    private boolean isMethod;

    public SymbolTableEntry(String type, boolean isMethod) {
        this.type = type;
        this.isMethod = isMethod;
    }

    public String getType() {
        return type;
    }

    public boolean isMethod() {
        return isMethod;
    }

    @Override
    public String toString() {
        return type + "(" + isMethod + ")";
    }
}
