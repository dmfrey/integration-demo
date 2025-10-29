package com.broadcom.springconsulting.integrationdemo.machineinterface.adapter.in.http;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.GetAllOutboundMachineInterfacesUseCase;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.in.UpdateMachineInterfaceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequestMapping( "/machineinterfaces" )
class MachineInterfaceController {

    private final GetAllOutboundMachineInterfacesUseCase getAllOutboundMachineInterfacesUseCase;
    private final UpdateMachineInterfaceUseCase updateMachineInterfaceUseCase;

    public MachineInterfaceController(
            GetAllOutboundMachineInterfacesUseCase getAllOutboundMachineInterfacesUseCase,
            UpdateMachineInterfaceUseCase updateMachineInterfaceUseCase
    ) {

        this.getAllOutboundMachineInterfacesUseCase = getAllOutboundMachineInterfacesUseCase;
        this.updateMachineInterfaceUseCase = updateMachineInterfaceUseCase;

    }

    @GetMapping
    public String index( Model model ) {

        var machineInterfaces = this.getAllOutboundMachineInterfacesUseCase.execute( new GetAllOutboundMachineInterfacesUseCase.GetAllOutboundMachineInterfacesCommand() );
        model.addAttribute( "machineInterfaces", machineInterfaces );

        return "index";
    }

    @PostMapping( "/{id}" )
    public ResponseEntity<?> edit(
            @PathVariable Long id,
            @RequestParam( "name" ) String name,
            @RequestParam( "connectionDirection" ) String connectionDirection,
            @RequestParam( "connectionType" ) String connectionType,
            @RequestParam( "hostname" ) String hostname,
            @RequestParam( "port" ) Integer port,
            @RequestParam( "username" ) String username,
            @RequestParam( "password" ) String password,
            @RequestParam( "remoteDirectory" ) String remoteDirectory
    ) {

        return updateMachineInterfaceUseCase.execute(
                new UpdateMachineInterfaceUseCase.UpdateMachineInterfaceCommand(
                        id, name, connectionDirection, connectionType,
                        hostname, port, username, password,
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
