package com.broadcom.springconsulting.integrationdemo.machine.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

import java.util.Optional;

public interface UpdateGatewayUseCase {

    Optional<Gateway> execute( UpdateGatewayCommand command );

    record UpdateGatewayCommand(
            Long id,
            String name,
            String connectionDirection,
            String connectionType,
            String hostname,
            Integer port,
            String username,
            String password,
            String remoteDirectory
    ) {

        public Gateway renderGateway() {

            return new Gateway(
                    id,
                    name,
                    connectionDirection,
                    connectionType,
                    hostname,
                    port,
                    username,
                    password,
                    remoteDirectory
            );
        }

    }
}
