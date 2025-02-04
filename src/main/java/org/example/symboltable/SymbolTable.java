package org.example.symboltable;
import lombok.Getter;
import org.example.symboltable.records.Class;
import org.example.symboltable.records.Method;
import org.example.symboltable.records.Program;
import org.example.symboltable.records.Variable;

import java.util.Map;

@Getter
public class SymbolTable {

	private final Program program;

	public SymbolTable() {
		program = new Program("Prog", "Program");
	}

	public Class findClass(String id) {
		return program.getClasses().get(id);
	}

	public void printSymbolTable(){
		Map<String, Class> allClasses = program.getClasses();
		for (Map.Entry<String, Class> c : allClasses.entrySet()) {
			Map<String, Method> allMethods = c.getValue().getMethods();
			Map<String, Variable> allGlobalVars = c.getValue().getGlobalVars();
			System.out.println("LEVEL 1: ClASS  " +  "TYPE: " + c.getValue().getType() + "\t IDENTITY: " + c.getValue().getId());
			for (Map.Entry<String,Variable> g : allGlobalVars.entrySet()) {
				System.out.println("|		 LEVEL 2: GLOBAL VARIABLE  " + "TYPE: " + g.getValue().getType() +  "\t IDENTITY: " + g.getValue().getId());
			}
			for (Map.Entry<String,Method> m : allMethods.entrySet()) {
				Map<String, Variable> allParams = m.getValue().getParameters();
				Map<String, Variable> allLocalVars = m.getValue().getLocalVars();
				System.out.println("|		 LEVEL 2: METHOD           " + "TYPE: " + m.getValue().getType() + "\t IDENTITY: " + m.getValue().getId());
				for (Map.Entry<String, Variable> p : allParams.entrySet()) {
					System.out.println("|	 	 |		LEVEL 3 PARAMETER:       " + "TYPE: " + p.getValue().getType() + "\t IDENTITY : " + p.getValue().getId());
				}
				for (Map.Entry<String, Variable> l : allLocalVars.entrySet()) {
					System.out.println("|	 	 |		LEVEL 3 LOCAL VARIABLE:  " + "TYPE: " + l.getValue().getType() +  "\t IDENTITY : " + l.getValue().getId());
				}
			}
		}
	}
}