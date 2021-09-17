package org.penitence.online.executor.core.hack;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author ren jie
 **/
@Slf4j
public class HackClassLoader extends ClassLoader {

    private static final HackClassLoader instance = new HackClassLoader();

    private final Collection<String> HACK_CLASS = Arrays.asList(
        "org.penitence.online.executor.core.hack.HackInputStream",
        "org.penitence.online.executor.core.hack.HackPrintStream",
        "org.penitence.online.executor.core.hack.HackSystem"
    );

    private HackClassLoader() {
        super(null);
        HACK_CLASS.forEach(c -> {
            try {
                byte[] b = findByteCodes(c);
                defineClass(null, b, 0, b.length);
            } catch (IOException e) {
                log.error("获取类:{} 的字节码失败", c);
            }
        });
    }

    private byte[] findByteCodes(String className) throws IOException {
        ClassReader r = new ClassReader(className);
        ClassWriter w = new ClassWriter(r, 0);
        r.accept(w, 0);
        return w.toByteArray();
    }

    public static HackClassLoader getInstance() {
        return instance;
    }
}
