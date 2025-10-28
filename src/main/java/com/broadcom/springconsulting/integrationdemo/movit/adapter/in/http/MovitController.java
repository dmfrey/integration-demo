package com.broadcom.springconsulting.integrationdemo.movit.adapter.in.http;

import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.PrepareRequestDownloadUseCase;
import com.broadcom.springconsulting.integrationdemo.movit.application.port.in.SendProgramUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( "/movit" )
class MovitController {

    private final PrepareRequestDownloadUseCase prepareRequestDownloadUseCase;
    private final SendProgramUseCase sendProgramUseCase;

    public MovitController( PrepareRequestDownloadUseCase prepareRequestDownloadUseCase, SendProgramUseCase sendProgramUseCase ) {

        this.prepareRequestDownloadUseCase = prepareRequestDownloadUseCase;
        this.sendProgramUseCase = sendProgramUseCase;

    }

    @GetMapping
    public String showMovit( Model model ) {

        var connections = this.prepareRequestDownloadUseCase.execute( new PrepareRequestDownloadUseCase.PrepareRequestDownloadCommand() );
        model.addAllAttributes( connections );
        model.addAttribute( "movitForm", new MovitController.MoveitForm() );

        return "movit";
    }

    @PostMapping( "/submit" )
    public String submitMovit( @ModelAttribute( "movitForm" ) MoveitForm moveitForm, Model model ) {

        this.sendProgramUseCase.execute( new SendProgramUseCase.SendProgramCommand( moveitForm.getGatewayPort(), moveitForm.getFilename(), moveitForm.getServerPort() ) );

        var connections = this.prepareRequestDownloadUseCase.execute( new PrepareRequestDownloadUseCase.PrepareRequestDownloadCommand() );
        model.addAllAttributes( connections );
        model.addAttribute( "movitForm", new MovitController.MoveitForm() );
        model.addAttribute( "message", "Download sent!" );

        return "movit";
    }

    static final class MoveitForm {

        private Integer serverPort;
        private String filename;
        private Integer gatewayPort;

        MoveitForm() { }

        public Integer getServerPort() {
            return serverPort;
        }

        public void setServerPort( Integer serverPort ) {
            this.serverPort = serverPort;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename( String filename ) {
            this.filename = filename;
        }

        public Integer getGatewayPort() {
            return gatewayPort;
        }

        public void setGatewayPort( Integer gatewayPort ) {
            this.gatewayPort = gatewayPort;
        }

    }

}
