package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "chat_channel")
public class Channel extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    /**
     * Creates a new {@link Channel} with a given name
     *
     * @param name the name of the channel
     * @return the newly generated channel
     */
    public static Channel createChannel(final String name) {
        final Channel channel = new Channel();
        channel.setName(name);

        channel.saveOrUpdate();
        return channel;
    }

    /**
     * Deletes a {@link Channel}
     *
     * @param id the {@link UUID} of the {@link Channel}
     */
    public static void removeChannel(final UUID id) {
        final Channel channel = getById(id);
        if (channel != null) {
            channel.delete();
        }
    }

    /**
     * Returns a {@link Channel} by it's UUID
     *
     * @param id the {@link UUID} of the Channel
     * @return the {@link Channel} which got the {@link UUID}
     */
    public static Channel getById(final UUID id) {
        return getById(Channel.class, id);
    }

    /**
     * Returns the name of the {@link Channel}
     *
     * @return the name of the {@link Channel}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the {@link Channel}
     *
     * @param name new Name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compares an {@link Object} if it equals the {@link Channel}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Channel} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Channel channel = (Channel) o;
        return Objects.equals(getName(), channel.getName()) &&
                Objects.equals(getId(), channel.getId());
    }

    /**
     * Hashes the {@link Channel} using {@link Objects} hash method
     *
     * @return Hash of the {@link Channel}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Channel} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .build();
    }
}
