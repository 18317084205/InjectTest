package com.liang.complier;

import com.squareup.javapoet.ClassName;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResTreeScanner extends com.sun.tools.javac.tree.TreeScanner {
    List<String> resourceIds = new ArrayList<>();

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
        Symbol symbol = jcFieldAccess.sym;
        if (symbol == null) {
            return;
        }
        ClassName className = ClassName.get(symbol.packge().getQualifiedName().toString(), "R",
                symbol.enclClass().name.toString());
//        System.out.println("className........: " + className);
        resourceIds.add(className + "." + symbol.name.toString());
    }

    @Override
    public void visitLiteral(JCTree.JCLiteral jcLiteral) {
        try {
            int value = (Integer) jcLiteral.value;
            resourceIds.add(value + "");
        } catch (Exception ignored) {
        }
    }

    public void reset() {
        resourceIds.clear();
    }

    public void defResIds(int[] values) {
        for (int id : values) {
            resourceIds.add(id + "");
        }
    }
}
