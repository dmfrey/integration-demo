package com.broadcom.springconsulting.integrationdemo.machine.application.port.out;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

import java.util.List;

public interface GatewayPort {

    List<Gateway> findAllServers();

    Gateway findByPort( Integer port );

}
