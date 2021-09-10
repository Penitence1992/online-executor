package org.penitence.online.executor.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.penitence.online.executor.core.modify.SystemReplaceMethodVisitor;

/**
 * 修改编译字节码, 确保安全性问题
 *
 * @author ren jie
 **/
public final class ClassSafetyModify {

    private ClassSafetyModify() {
    }

    public static byte[] process(byte[] classes) {
        ClassReader cr = new ClassReader(classes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new SystemReplaceMethodVisitor(writer);
        cr.accept(cv, ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }
}
