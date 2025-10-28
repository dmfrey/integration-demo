package com.broadcom.springconsulting.integrationdemo.machine.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

public interface UpdateGatewayUseCase {

    Gateway execute(UpdateGatewayRecord record);

    record UpdateGatewayRecord(
            Long id,
            String name,
            String connectionType,
            String hostname,
            Integer port,
            String username,
            String password,
            String remoteDirectory
    ){}
}
