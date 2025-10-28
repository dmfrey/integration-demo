package com.broadcom.springconsulting.integrationdemo.movit.application.port.in;

import java.util.Map;

public interface PrepareRequestDownloadUseCase {

    Map<String, Object> execute( PrepareRequestDownloadCommand command );

    record PrepareRequestDownloadCommand() { }

}
