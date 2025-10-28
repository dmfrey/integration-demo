package com.broadcom.springconsulting.integrationdemo.movit.web.controller;

import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.GetAllServersUseCase;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.in.UpdateGatewayUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GatewayListController {

    private final GetAllServersUseCase getAllServersUseCase;
    private final UpdateGatewayUseCase updateGatewayUseCase;

    public GatewayListController(GetAllServersUseCase getAllServersUseCase,
                                 UpdateGatewayUseCase updateGatewayUseCase) {
        this.getAllServersUseCase = getAllServersUseCase;
        this.updateGatewayUseCase = updateGatewayUseCase;
    }

    @GetMapping("/")
    public String viewHomePage(Model model) {

        var gateways = getAllServersUseCase.execute(
                new GetAllServersUseCase.GetAllServersCommand()
        );
        model.addAttribute("gateways", gateways);
        return "index";
    }

    @PostMapping("/gateways/{id}/edit")
    public String editGateway(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("connectionType") String connectionType,
            @RequestParam("hostname") String hostname,
            @RequestParam("port") Integer port,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("remoteDirectory") String remoteDirectory
            ) {
        try {
            updateGatewayUseCase.execute(
                    new UpdateGatewayUseCase.UpdateGatewayRecord(
                            id,
                            name,
                            connectionType,
                            hostname,
                            port,
                            username,
                            password,
                            remoteDirectory
                    )
            );
        } catch (Exception ex) {
            return "Damn!";
        }

        return "OK!";
    }
}
