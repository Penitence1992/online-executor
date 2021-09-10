# 航天标准服务

由定制的脚手架创建的服务, 提供了如下内容:

1. 标准的基于`spring boot` + `god` 的`gitlab ci`内容

2. 提供了标准的`.editorconfig`文件, 配置了习惯缩进内容

3. 添加了内部maven仓库`https://maven.ascs.tech`

4. 提供了标准的二方库

5. 提供了Makefile和关于god工程的配置

## 注意

项目初始化完成以后请通过

```bash
git init
git submodule add -f ssh://git@git.ascs.tech/base/god.git git_modules/god
```

添加`god`工程, 如果没有权限, 请向管理员索取

同时如果把项目镜像到gitlab上进行ci构建, 请在项目页面中的`用户设置`->`CI/CD`->`变量` 中, 添加`DEPLOY_SSH_KEY`的变量, 内容为你拥有权限的私钥
