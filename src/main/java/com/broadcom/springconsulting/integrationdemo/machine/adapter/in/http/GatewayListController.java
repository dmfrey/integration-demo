package com.broadcom.springconsulting.integrationdemo.machine.adapter.in.http;

import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllGatewayUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.UpdateGatewayUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
class GatewayListController {

    private final GetAllGatewayUseCase getAllGatewayUseCase;
    private final UpdateGatewayUseCase updateGatewayUseCase;

    public GatewayListController( GetAllGatewayUseCase getAllGatewayUseCase,
                                 UpdateGatewayUseCase updateGatewayUseCase ) {
        this.getAllGatewayUseCase = getAllGatewayUseCase;
        this.updateGatewayUseCase = updateGatewayUseCase;
    }

    @GetMapping( "/" )
    public String viewHomePage( Model model ) {

        var gateways = this.getAllGatewayUseCase.execute(
                new GetAllGatewayUseCase.GetAllGatewaysCommand()
        );
        model.addAttribute("gateways", gateways);

        return "index";
    }

    @PostMapping( "/gateways/{id}" )
    public ResponseEntity<?> editGateway(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("connectionDirection") String connectionDirection,
            @RequestParam("connectionType") String connectionType,
            @RequestParam("hostname") String hostname,
            @RequestParam("port") Integer port,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("remoteDirectory") String remoteDirectory
    ) {

        return updateGatewayUseCase.execute(
                new UpdateGatewayUseCase.UpdateGatewayCommand(
                        id,
                        name,
                        connectionDirection,
                        connectionType,
                        hostname,
                        port,
                        username,
                        password,
                        remoteDirectory
                )
        )
                .map( gateway ->
                        ResponseEntity.accepted()
                                .location( ServletUriComponentsBuilder.fromCurrentRequest().path( "/{id}" ).buildAndExpand( gateway.id() ).toUri() )
                                .build()
                )
                .orElse( ResponseEntity.badRequest().build() );
    }

}
