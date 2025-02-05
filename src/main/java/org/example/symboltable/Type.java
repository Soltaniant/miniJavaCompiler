package org.example.symboltable;

import lombok.Getter;
import org.example.symboltable.records.Class;

public class Type {

    private Class c;

    @Getter
    private final String elementType;

    public Type(String elementType) {
        this.elementType = elementType;
    }

    public Type(Class c) {
        this.elementType = "class";
        this.c = c;
    }

    public Class getObject() {
        if (!this.elementType.equals("class")) {
            return null;
        }
        return this.c;
    }
}
