package net.cryptic_game.backend.data.user;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity representing an user entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "user")
@Data
public final class User extends TableModelAutoId {

    @Column(name = "username", updatable = true, nullable = false, unique = true)
    private String username;

    @JsonTransient
    @Column(name = "password", updatable = true, nullable = false)
    private String passwordHash;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    @Column(name = "last", updatable = true, nullable = false)
    private OffsetDateTime last;

    /**
     * Create a {@link User} with the given user data.
     *
     * @param session the sql session
     * @param username The username of the new user
     * @param password The password of the new user
     * @return The instance of the created {@link User}
     */
    public static User createUser(final Session session, final String username, final String password) {
        final OffsetDateTime now = OffsetDateTime.now();

        final User user = new User();
        user.setUsername(username);
        user.setCreated(now);
        user.setLast(now);
        user.setPassword(password);

        user.saveOrUpdate(session);
        return user;
    }

    /**
     * Fetches the {@link User} with the given id.
     *
     * @param session the sql session
     * @param id The id of the {@link User}
     * @return The instance of the fetched {@link User} if it exists | null if the entity does not exist
     */
    public static User getById(final Session session, final UUID id) {
        return getById(session, User.class, id);
    }

    /**
     * Fetches the {@link User} with the give name.
     *
     * @param session the sql session
     * @param username The username of the user
     * @return The instance of the fetched {@link User} if it exists | null if the {@link User} does not exist
     */
    public static User getByUsername(final Session session, final String username) {
        return session.createQuery("select object (u) from User as u where u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream().findFirst().orElse(null);
    }

    /**
     * Set a new password for the given {@link User}.
     *
     * @param newPassword New password to be set
     */
    public void setPassword(final String newPassword) {
        this.setPasswordHash(SecurityUtils.hash(newPassword));
    }

    /**
     * Checks whether a password matches the current password of the {@link User}.
     *
     * @param input The password to be checked
     * @return true if the password is correct | false if the password is wrong
     */
    public boolean verifyPassword(final String input) {
        return SecurityUtils.verify(input, this.getPasswordHash());
    }

    /**
     * @param session the sql session
     * Deletes the {@link User}.
     */
    @Override
    public void delete(final Session session) {
        net.cryptic_game.backend.data.user.Session.getByUser(session, this).forEach(i -> i.delete(session));
        super.delete(session);
    }

    public JsonObject serializePublic() {
        return JsonBuilder.create("id", this.getId())
                .add("username", this.getUsername())
                .add("created", this.getCreated())
                .build();
    }
}
