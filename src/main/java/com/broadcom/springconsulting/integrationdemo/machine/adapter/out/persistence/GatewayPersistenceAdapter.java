package com.broadcom.springconsulting.integrationdemo.machine.adapter.out.persistence;

import com.broadcom.springconsulting.integrationdemo.machine.application.domain.model.Gateway;
import com.broadcom.springconsulting.integrationdemo.machine.application.port.out.GatewayPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@Transactional
class GatewayPersistenceAdapter implements GatewayPort {

    private final GatewayRepository gatewayRepository;

    GatewayPersistenceAdapter( GatewayRepository gatewayRepository ) {

        this.gatewayRepository = gatewayRepository;

    }

    @Override
    public List<Gateway> findAllInboundServers() {

        var results = this.gatewayRepository.findAllInboundServers();

        return StreamSupport.stream( results.spliterator(), false )
                .map( this::fromEntity )
                .toList();
    }

    @Override
    public List<Gateway> findAllOutboundGateways() {

        var results = this.gatewayRepository.findAllOutboundGateways();

        return StreamSupport.stream( results.spliterator(), false )
                .map( this::fromEntity )
                .toList();
    }

    @Override
    public Gateway findByPort( Integer port ) {

        return this.gatewayRepository.findByPort( port )
                .map( this::fromEntity )
                .orElse( null );
    }

    @Override
    public Optional<Gateway> updateGateway( Gateway gateway ) {

        var updated = this.gatewayRepository.save( toEntity( gateway ) );

        return Optional.of( updated )
                .map( this::fromEntity );
    }

    private Gateway fromEntity(GatewayEntity entity) {

        return new Gateway(
                entity.id(),
                entity.name(),
                entity.connectionDirection().name(),
                entity.connectionType().name(),
                entity.serverHostname(),
                entity.port(),
                entity.username(),
                entity.password(),
                entity.remoteDirectory()
        );
    }

    private GatewayEntity toEntity( Gateway gateway ) {

        return new GatewayEntity(
                gateway.id(),
                gateway.name(),
                ConnectionDirection.valueOf( gateway.connectionDirection() ),
                ConnectionType.valueOf( gateway.connectionType() ),
                gateway.hostname(),
                gateway.port(),
                gateway.username(),
                gateway.password(),
                gateway.remoteDirectory()
        );

    }
}
