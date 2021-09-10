package org.penitence.online.executor.core;

import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author ren jie
 **/
@Data
@Builder
public class ExecutorResult {

    private ByteArrayOutputStream out;

    private ByteArrayOutputStream err;

    private Boolean success;

    private List<String> errors;
}
