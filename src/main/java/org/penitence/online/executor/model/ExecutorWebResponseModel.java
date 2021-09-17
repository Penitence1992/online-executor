package org.penitence.online.executor.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.penitence.online.executor.core.ExecutorResult;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/**
 * @author ren jie
 **/
@Getter
@Setter
@Builder
@ToString
public class ExecutorWebResponseModel {

    private String out;

    private String err;

    private Boolean success;

    private List<String> errors;


    public static ExecutorWebResponseModel fromExecutorResult(ExecutorResult res) {
        return ExecutorWebResponseModel.builder()
            .out(fetchByteArrayValue(res.getOut()))
            .err(fetchByteArrayValue(res.getErr()))
            .success(res.getSuccess())
            .errors(res.getErrors()).build();
    }

    private static String fetchByteArrayValue(ByteArrayOutputStream bos) {
        return Optional.ofNullable(bos)
            .map(b -> b.toString(StandardCharsets.UTF_8))
            .orElse("");
    }
}
