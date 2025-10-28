package com.broadcom.springconsulting.integrationdemo.machine.application.port.out;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

import java.util.List;
import java.util.Optional;

public interface GatewayPort {

    List<Gateway> findAllInboundServers();

    List<Gateway> findAllOutboundGateways();

    Gateway findByPort( Integer port );

    Optional<Gateway> updateGateway( Gateway gateway );

}
