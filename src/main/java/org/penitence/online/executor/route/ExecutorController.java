package org.penitence.online.executor.route;

import org.penitence.online.executor.core.JavaExecutor;
import org.penitence.online.executor.model.ExecutorWebResponseModel;
import org.penitence.online.executor.model.RequestExecutorBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ren jie
 **/
@RequestMapping("/v1/executor")
@RestController
public class ExecutorController {

    @PostMapping("/java")
    public ExecutorWebResponseModel executeJava(@RequestBody RequestExecutorBody body) {
        return ExecutorWebResponseModel.fromExecutorResult(JavaExecutor.executor(body.getCode(), null));
    }
}
