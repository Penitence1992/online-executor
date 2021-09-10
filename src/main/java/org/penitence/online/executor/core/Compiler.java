package org.penitence.online.executor.core;

import org.penitence.online.executor.core.exception.CompileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author ren jie
 **/
public interface Compiler {

    List<byte[]> compiler(InputStream is) throws IOException, CompileException;
}
