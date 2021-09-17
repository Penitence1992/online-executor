package org.penitence.online.executor.core;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * @author ren jie
 **/
class JavaExecutorTest {

    @Test
    void testExecutor() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        InputStream is = new ClassPathResource("HelloWorld.java").getInputStream();
        String code = FileCopyUtils.copyToString(new InputStreamReader(is));
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++){
            ExecutorResult res = JavaExecutor.executor(code, new String[]{"1","2"});
            if (res.getSuccess()) {
                System.out.println("执行成功");
                String out = res.getOut().toString(StandardCharsets.UTF_8);
                String err = res.getErr().toString(StandardCharsets.UTF_8);
                System.out.println("成功的输出:");
                System.out.println(out);
                System.out.println("失败的输出:");
                System.out.println(err);
            }else {
                res.getErrors().forEach(System.out::println);
            }
        }
        System.out.println("使用时间: " + (System.currentTimeMillis() - start));
    }
}
