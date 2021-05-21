package net.cryptic_game.backend.data.sql.repositories.network;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NetworkRepository extends JpaRepository<Network, UUID> {

    Optional<Network> findByName(String name);

    List<Network> findAllByOwner(Device owner);

    List<Network> findAllByIsPublicTrue();

    default Network create(final String name, final Device owner, final boolean isPublic) {
        Network network = new Network();
        network.setName(name);
        network.setOwner(owner);
        network.setPublic(isPublic);
        network.setCreated(OffsetDateTime.now());

        return this.save(network);
    }
}
