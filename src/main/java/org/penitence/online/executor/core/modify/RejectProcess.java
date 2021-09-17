package org.penitence.online.executor.core.modify;

import org.penitence.online.executor.core.exception.RejectException;

import java.util.Set;

/**
 * 拒绝调用某些方法或者属性
 * @author ren jie
 **/
public final class RejectProcess {

    private static final Set<String> REJECT_METHOD_OWNER = Set.of(
        "java/lang/Thread"
    );

    private RejectProcess(){

    }



    public static void checkMethodVisitor(int opcode, String owner, String name, String descriptor) {
        if (REJECT_METHOD_OWNER.contains(owner)) {
            throw new RejectException("拒绝调用: " + owner.replace("/", ".") + "的类");
        }
    }
}
