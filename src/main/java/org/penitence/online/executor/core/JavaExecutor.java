package org.penitence.online.executor.core;

import org.penitence.online.executor.core.exception.CompileException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author ren jie
 **/
public final class JavaExecutor {

    private static final int RUN_TIME_LIMITED = 1500;

    private static final Compiler JAVA_COMPILER = new JavaStringCompiler();

    private static final ExecutorService pool = new ThreadPoolExecutor(10, 10,
        0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));

    public static ExecutorResult executor(String code, String[] args) {
        try {
            Future<ExecutorResult> future = pool.submit(() -> call(code, args));
            return future.get(RUN_TIME_LIMITED, TimeUnit.SECONDS);
        } catch (RejectedExecutionException e) {
            throw new RuntimeException("等待任务已满");
        } catch (ExecutionException e) {
            return ExecutorResult
                .builder().success(false).errors(Collections.singletonList(e.getCause().getMessage())).build();
        } catch (InterruptedException e) {
            return ExecutorResult
                .builder().success(false).errors(Collections.singletonList("服务中止")).build();
        } catch (TimeoutException e) {
            return ExecutorResult
                .builder().success(false).errors(Collections.singletonList("代码执行超时")).build();
        }

    }

    private static ExecutorResult call(String code, String[] args) {
        try {
            List<byte[]> classes = JAVA_COMPILER.compiler(new ByteArrayInputStream(code.getBytes(StandardCharsets.UTF_8)));
            MemoryClassLoader classLoader = new MemoryClassLoader();
            classes.forEach(classLoader::loadByte);
            try {
                String className = JavaStringCompiler.findClassName(code);
                String fullClassName = Optional.ofNullable(JavaStringCompiler.findPackageName(code))
                    .map(p -> p + ".")
                    .map(p -> p + className)
                    .orElse(className);
                Class<?> clz = classLoader.loadClass(fullClassName);

                Method method = clz.getMethod("main", String[].class);
                method.invoke(null, (Object) args);
                ByteArrayOutputStream[] data = findSandBoxByteArrayOutputStream(classLoader);
                return ExecutorResult.builder()
                    .err(Optional.ofNullable(data[1]).orElseGet(ByteArrayOutputStream::new))
                    .out(Optional.ofNullable(data[0]).orElseGet(ByteArrayOutputStream::new))
                    .success(true)
                    .build();
            } catch (NoSuchMethodException e) {
                return ExecutorResult.builder().success(false).errors(Collections.singletonList("缺少main方法")).build();
            } catch (InvocationTargetException e) {
                return ExecutorResult.builder().success(false).errors(
                    Arrays.asList("方法调用出错:",
                        "\t" + e.getTargetException().getClass().getName() + ":" + e.getTargetException().getMessage())
                ).build();
            } catch (IllegalAccessException e) {
                return ExecutorResult.builder().success(false).errors(Collections.singletonList("main方法没有设置为public")).build();
            } catch (ClassNotFoundException e) {
                return ExecutorResult.builder().success(false).errors(Collections.singletonList(e.getMessage())).build();
            }
        } catch (IOException e) {
            //ignore
            return ExecutorResult.builder().success(false).build();
        } catch (CompileException ce) {
            List<String> errors = new LinkedList<>();
            ce.getCollector().getDiagnostics().forEach(d -> {
                errors.add(d.toString());
            });
            return ExecutorResult.builder().success(false).errors(errors).build();
        }
    }

    private static ByteArrayOutputStream[] findSandBoxByteArrayOutputStream(MemoryClassLoader classLoader) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> c = classLoader.loadClass("org.penitence.online.executor.core.hack.HackSystem");
        Class<?> psc = classLoader.loadClass("org.penitence.online.executor.core.hack.HackPrintStream");
        try {
            Object out = c.getDeclaredField("out").get(null);
            Object err = c.getDeclaredField("err").get(null);
            ByteArrayOutputStream[] res = new ByteArrayOutputStream[]
                {
                    (ByteArrayOutputStream) psc.getDeclaredMethod("getOutputStream").invoke(out),
                    (ByteArrayOutputStream) psc.getDeclaredMethod("getOutputStream").invoke(err)

                };
            psc.getDeclaredMethod("close").invoke(out);
            psc.getDeclaredMethod("close").invoke(err);
            return res;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return new ByteArrayOutputStream[]
            {
                new ByteArrayOutputStream(),
                new ByteArrayOutputStream()

            };
    }

}
