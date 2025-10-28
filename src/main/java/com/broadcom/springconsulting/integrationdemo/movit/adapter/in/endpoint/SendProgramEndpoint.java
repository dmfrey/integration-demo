package com.broadcom.springconsulting.integrationdemo.movit.adapter.in.endpoint;

import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.SendProgramUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SendProgramEndpoint {

    private final SendProgramUseCase sendProgramUseCase;

    public SendProgramEndpoint( SendProgramUseCase sendProgramUseCase ) {

        this.sendProgramUseCase = sendProgramUseCase;

    }

    @PostMapping( "/{port}")
    public ResponseEntity<String> sendProgramToPort( @PathVariable( "port" ) int port, @RequestParam String filename, @RequestParam Integer serverPort ) {

        this.sendProgramUseCase.execute( new SendProgramUseCase.SendProgramCommand( port, filename, serverPort ) );

        return ResponseEntity
                .accepted()
                .build();
    }

}
