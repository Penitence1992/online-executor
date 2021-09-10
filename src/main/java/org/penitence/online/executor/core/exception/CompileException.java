package org.penitence.online.executor.core.exception;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * @author ren jie
 **/
public class CompileException extends Exception {

    private final DiagnosticCollector<JavaFileObject> collector;

    public CompileException(String message, DiagnosticCollector<JavaFileObject> collector) {
        super(message);
        this.collector = collector;
    }

    public DiagnosticCollector<JavaFileObject> getCollector() {
        return collector;
    }
}
