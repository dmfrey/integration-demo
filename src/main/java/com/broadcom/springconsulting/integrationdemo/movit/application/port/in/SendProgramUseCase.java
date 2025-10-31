package com.broadcom.springconsulting.integrationdemo.movit.application.port.in;

import com.broadcom.springconsulting.integrationdemo.movit.application.domain.model.DownloadComplete;

public interface SendProgramUseCase {

    DownloadComplete execute( SendProgramCommand command );

    record SendProgramCommand( Integer port, String filename, Integer serverPort ) { }

}
