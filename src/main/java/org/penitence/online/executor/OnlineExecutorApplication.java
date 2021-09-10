package org.penitence.online.executor;

import cn.cityworks.cityworkscore.domain.ResponseDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OnlineExecutorApplication {

    public static void main(String[] args) {
        new ResponseDTO();
        SpringApplication.run(OnlineExecutorApplication.class, args);
    }

}
