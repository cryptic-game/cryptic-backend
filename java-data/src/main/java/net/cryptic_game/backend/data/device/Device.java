package net.cryptic_game.backend.data.device;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Entity representing a device entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "device_device")
@Data
public final class Device extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    @Column(name = "powered_on", updatable = true, nullable = false)
    private boolean poweredOn;

    /**
     * Creates a new {@link Device}.
     *
     * @param session the sql session
     * @param name      Name of the {@link Device}
     * @param owner     Owner of the {@link Device}
     * @param poweredOn Power state of the {@link Device}
     * @return The instance of the created {@link Device}
     */
    public static Device createDevice(final Session session, final String name, final User owner, final boolean poweredOn) {
        final Device device = new Device();
        device.setName(name);
        device.setOwner(owner);
        device.setPoweredOn(poweredOn);

        device.saveOrUpdate(session);
        return device;
    }

    /**
     * Fetches the {@link Device} with the given id.
     *
     * @param session the sql session
     * @param id The id of the {@link Device}
     * @return The instance of the fetched {@link Device} if it exists | null if the entity does not exist
     */
    public static Device getById(final Session session, final UUID id) {
        return getById(session, Device.class, id);
    }

    public boolean hasAccess(final Session session, final User user) {
        return getOwner().equals(user) || DeviceAccess.hasAccess(session, user, this);
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Device} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("owner", this.getOwner().getId())
                .add("powered_on", this.isPoweredOn())
                .build();
    }
}
