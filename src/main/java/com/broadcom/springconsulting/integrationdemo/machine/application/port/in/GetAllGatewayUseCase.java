package com.broadcom.springconsulting.integrationdemo.machine.application.port.in;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;

import java.util.List;

public interface GetAllGatewayUseCase {

    List<Gateway> execute( GetAllGatewaysCommand command );

    record GetAllGatewaysCommand() {}

}
