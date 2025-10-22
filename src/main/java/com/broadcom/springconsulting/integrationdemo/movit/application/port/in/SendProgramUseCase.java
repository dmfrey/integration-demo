package com.broadcom.springconsulting.integrationdemo.movit.application.port.in;

public interface SendProgramUseCase {

    void execute( SendProgramCommand command );

    record SendProgramCommand( Integer port ) { }

}
