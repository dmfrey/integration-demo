package com.broadcom.springconsulting.integrationdemo.machineinterface.adapter.out.persistence;

import com.broadcom.springconsulting.integrationdemo.machineinterface.application.domain.model.MachineInterface;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindAllOutboundMachineInterfacesPort;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindByPortNumberMachineInterfacePort;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.FindAllInboundMachineInterfacesPort;
import com.broadcom.springconsulting.integrationdemo.machineinterface.application.port.out.UpdateMachineInterfacePort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@Transactional
class MachineInterfacePersistenceAdapter
        implements
        FindAllInboundMachineInterfacesPort, FindAllOutboundMachineInterfacesPort,
        FindByPortNumberMachineInterfacePort, UpdateMachineInterfacePort
{

    private final MachineInterfaceRepository machineInterfaceRepository;

    MachineInterfacePersistenceAdapter( MachineInterfaceRepository machineInterfaceRepository ) {

        this.machineInterfaceRepository = machineInterfaceRepository;

    }

    @Override
    public List<MachineInterface> findAllInbound() {

        var results = this.machineInterfaceRepository.findAllByConnectionDirection( ConnectionDirection.INBOUND );

        return StreamSupport.stream( results.spliterator(), false )
                .map( this::fromEntity )
                .toList();
    }

    @Override
    public List<MachineInterface> findAllOutbound() {

        var results = this.machineInterfaceRepository.findAllByConnectionDirection( ConnectionDirection.OUTBOUND );

        return StreamSupport.stream( results.spliterator(), false )
                .map( this::fromEntity )
                .toList();
    }

    @Override
    public Optional<MachineInterface> findByPort( Integer port ) {

        return this.machineInterfaceRepository.findByPort( port )
                .map( this::fromEntity );
    }

    @Override
    public Optional<MachineInterface> update( MachineInterface machineInterface ) {

        var updated = this.machineInterfaceRepository.save( toEntity(machineInterface) );

        return Optional.of( updated )
                .map( this::fromEntity );
    }

    private MachineInterface fromEntity(MachineInterfaceEntity entity) {

        return new MachineInterface(
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

    private MachineInterfaceEntity toEntity(MachineInterface machineInterface) {

        return new MachineInterfaceEntity(
                machineInterface.id(),
                machineInterface.name(),
                ConnectionDirection.valueOf( machineInterface.connectionDirection() ),
                ConnectionType.valueOf( machineInterface.connectionType() ),
                machineInterface.hostname(),
                machineInterface.port(),
                machineInterface.username(),
                machineInterface.password(),
                machineInterface.remoteDirectory()
        );

    }
}
