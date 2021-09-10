package org.penitence.test;

import java.io.IOException;

public class HelloWorld {

    public static void main(String[] args) throws IOException {

        if (args == null || args.length != 2) {
            System.out.println("请输入两个需要求和的数字");
            return;
        }

        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        System.out.println("sum result = " + new Sum(a, b).sum());
    }

    public static class Sum {

        private int a;
        private int b;

        public Sum(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int sum() {
            System.out.printf("计算 %d + %d = %d", a, b, a + b);
            return a + b;
        }

    }

}
