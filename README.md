# Java代码在线执行

使用Java内自带的编译器工具对代码执行内存编译, 并且把编译后的代码放入一个沙箱ClassLoader内,

通过Asm字节码来修改代码调用的System和Thread的类, 来拦截输出和组织调用某些类
