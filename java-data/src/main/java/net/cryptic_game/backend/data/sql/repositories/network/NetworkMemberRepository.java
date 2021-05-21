package net.cryptic_game.backend.data.sql.repositories.network;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.network.NetworkMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetworkMemberRepository extends JpaRepository<NetworkMember, UUID> {

    Optional<NetworkMember> findByKeyDeviceAndKeyNetwork(Device device, Network network);

    List<NetworkMember> findAllByKeyDevice(Device device);

    List<NetworkMember> findAllByKeyNetwork(Network network);

    default NetworkMember create(final Network network, final Device device) {
        NetworkMember existingMember = findByKeyDeviceAndKeyNetwork(device, network).orElse(null);
        if (existingMember != null) return existingMember;

        NetworkMember networkMember = new NetworkMember();
        networkMember.setNetwork(network);
        networkMember.setDevice(device);

        return this.save(networkMember);
    }

    @Transactional
    @Modifying
    void deleteAllByKeyNetwork(Network network);
}
