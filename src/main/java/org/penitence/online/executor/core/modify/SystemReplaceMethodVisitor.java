package org.penitence.online.executor.core.modify;


import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.penitence.online.executor.core.exception.RejectException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ren jie
 **/
@Slf4j
public class SystemReplaceMethodVisitor extends ClassVisitor {
    public SystemReplaceMethodVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new InnerMethodVisitor(Opcodes.ASM9, mv);
    }

    private static class InnerMethodVisitor extends MethodVisitor {

        private final Set<Label> visitedLabels = new HashSet<>();

        private static final String SYSTEM_HACK = "org/penitence/online/executor/core/hack/HackSystem";

        private static final String SYSTEM = "java/lang/System";

        private static final String SYSTEM_HACK_PRINT_STREAM = "org/penitence/online/executor/core/hack/HackPrintStream";
        private static final String SYSTEM_HACK_INPUT_STREAM = "org/penitence/online/executor/core/hack/HackInputStream";


        public InnerMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (log.isDebugEnabled()) {
                log.debug("visitJumpInsn: " + label.toString());
            }
            if (visitedLabels.contains(label)) {
                log.debug("Op: " + opcode + ", GOTO to previous command - possible looped execution");
                throw new RejectException("拒绝调用循环语句");
            }
            super.visitJumpInsn(opcode, label);
        }

        @Override
        public void visitLabel(Label label) {
            if (log.isDebugEnabled()) {
                log.debug("visitLabel " + label.toString());
            }
            visitedLabels.add(label);
            super.visitLabel(label);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            if (log.isDebugEnabled()) {
                log.debug("visitLineNumber " + start.toString());
            }
            visitedLabels.add(start);
            super.visitLineNumber(line, start);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (log.isDebugEnabled()) {
                log.debug("method opcode : {}, owner: {}, name: {}, descriptor: {}", opcode, owner, name, descriptor);
            }
            RejectProcess.checkMethodVisitor(opcode, owner, name, descriptor);
            if (owner.equals("java/io/PrintStream") && opcode == Opcodes.INVOKEVIRTUAL) {
                if (log.isDebugEnabled()) {
                    log.debug("change method insn, change source owner {} to {}", owner, SYSTEM_HACK_PRINT_STREAM);
                }
                super.visitMethodInsn(opcode, SYSTEM_HACK_PRINT_STREAM, name, descriptor, isInterface);
                return;
            } else if (owner.equals(SYSTEM)) {
                if (log.isDebugEnabled()) {
                    log.debug("change method insn, change source owner {} to {}", owner, SYSTEM_HACK);
                }
                super.visitMethodInsn(opcode, SYSTEM_HACK, name, descriptor, isInterface);
                return;
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (owner.equals("java/lang/System") && opcode == Opcodes.GETSTATIC) {
                if (name.equals("out") || name.equals("err")) {
                    if (log.isDebugEnabled()) {
                        log.debug("change field insn, change source owner {} to {}", owner, SYSTEM_HACK);
                    }
                    super.visitFieldInsn(opcode, SYSTEM_HACK, name, "L" + SYSTEM_HACK_PRINT_STREAM + ";");
                    return;
                }
            }
            super.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }
}
