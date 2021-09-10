package org.penitence.online.executor.core;

import lombok.extern.slf4j.Slf4j;
import org.penitence.online.executor.core.exception.CompileException;
import org.springframework.util.FileCopyUtils;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ren jie
 **/
@Slf4j
public class JavaStringCompiler implements Compiler {


    /**
     * 使用 Pattern 预编译功能
     */
    private static final Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([$_a-zA-Z][.$_a-zA-Z0-9]*)\\s*;");

    @Override
    public List<byte[]> compiler(InputStream is) throws IOException, CompileException {
        String source = FileCopyUtils.copyToString(new InputStreamReader(is));
        DiagnosticCollector<JavaFileObject> compileCollector = new DiagnosticCollector<>(); // 编译结果收集器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        CacheJavaFileManager manager = new CacheJavaFileManager(compiler.getStandardFileManager(compileCollector, null, StandardCharsets.UTF_8));

        String className = findClassName(source);

        SourceJavaFileObject javaFileObject = new SourceJavaFileObject(className, source);

        Boolean result = compiler.getTask(null, manager, compileCollector, null, null, Arrays.asList(javaFileObject)).call();

        if (result == null || !result) {
            throw new CompileException("编译错误", compileCollector);
        }

        return manager.getOutputJavaFileObject().stream()
            .map(f -> ClassSafetyModify.process(f.getContent()))
            .collect(Collectors.toList());
    }

    public static String findClassName(String source) {
        // 从源码字符串中匹配类名
        Matcher matcher = CLASS_PATTERN.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("No valid class");
        }
    }

    public static String findPackageName(String source) {
        Matcher matcher = PACKAGE_PATTERN.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("No valid package");
        }
    }


    public static class CacheJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        private List<SourceJavaFileObject> fileObjects = new LinkedList<>();

        /**
         * Creates a new instance of ForwardingJavaFileManager.
         *
         * @param fileManager delegate to this file manager
         */
        protected CacheJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            if (log.isDebugEnabled()) {
                log.debug("getJavaFileForOutput location: {}, className: {}, kind: {}, sibling: {}", location, className, kind, sibling);
            }
            SourceJavaFileObject file = new SourceJavaFileObject(className, kind);
            fileObjects.add(file);
            return file;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return new MemoryClassLoader();
        }

        public List<SourceJavaFileObject> getOutputJavaFileObject() {
            return fileObjects;
        }
    }

    public static class SourceJavaFileObject extends SimpleJavaFileObject {

        private String source;

        private ByteArrayOutputStream outputStream;

        protected SourceJavaFileObject(String name, String source) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        protected SourceJavaFileObject(String name, Kind kind) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), kind);
            this.source = null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            if (source == null) {
                throw new IllegalArgumentException("source == null");
            }
            return source;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            outputStream = new ByteArrayOutputStream();
            return outputStream;
        }

        public byte[] getContent() {
            return outputStream.toByteArray();
        }
    }
}
