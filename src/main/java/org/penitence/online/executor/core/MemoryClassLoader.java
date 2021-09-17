package org.penitence.online.executor.core;

import org.penitence.online.executor.core.hack.HackClassLoader;

/**
 * @author ren jie
 **/
public class MemoryClassLoader extends ClassLoader {

    public MemoryClassLoader() {
        super(HackClassLoader.getInstance());
    }

    public Class<?> loadByte(byte[] classBytes) {
        return defineClass(null, classBytes, 0, classBytes.length);
    }
}
