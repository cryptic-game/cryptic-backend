package net.cryptic_game.backend.data.user;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a session entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "session")
public class Session extends TableModelAutoId implements JsonSerializable {

    private static final Duration EXPIRE = Duration.of(AppBootstrap.getInstance().getConfig().getSessionExpire(), ChronoUnit.DAYS);

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "token", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private UUID token;

    @Column(name = "device_name", nullable = false, updatable = false)
    private String deviceName;

    @Column(name = "expire", nullable = false, updatable = false)
    private ZonedDateTime expire;

    @Column(name = "last_active", nullable = false, updatable = true)
    private ZonedDateTime lastActive;

    /**
     * Empty constructor to create a new {@link Session}
     */
    public Session() {
    }

    /**
     * Creates a new {@link Session}
     *
     * @param user       {@link User} of the {@link Session}
     * @param token      token of the {@link Session}
     * @param deviceName device name of the {@link Session}
     * @return The instance of the created {@link Session}
     */
    public static Session createSession(final User user, final UUID token, final String deviceName) {
        final Session session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setDeviceName(deviceName);
        session.setExpire(ZonedDateTime.now().plus(EXPIRE));
        session.setLastActive(ZonedDateTime.now());

        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(session);
        sqlSession.getTransaction().commit();
        sqlSession.close();

        return session;
    }

    /**
     * Fetches the {@link Session} with the given id
     *
     * @param id The id of the {@link Session}
     * @return The instance of the fetched {@link Session} if it exists | null if the entity does not exist
     */
    public static Session getById(final UUID id) {
        return getById(Session.class, id);
    }

    /**
     * Fetches all {@link Session}s of a specific {@link User}
     *
     * @param user The user whose {@link Session}s are to be fetched
     * @return {@link List} containing {@link Session}s
     */
    public static List<Session> getByUser(User user) {
        try (org.hibernate.Session sqlSession = sqlConnection.openSession()) {
            return sqlSession.createQuery("select object (s) from Session as s where s.user.id = :userId", Session.class)
                    .setParameter("userId", user.getId())
                    .getResultList();
        }
    }

    /**
     * Deletes all expired and invalid {@link Session}s of a {@link User}
     *
     * @param user The {@link User} whose {@link Session}s are to be deleted.
     */
    public static void deleteExpiredSessions(final User user) {
        final org.hibernate.Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.createQuery("delete from Session as s where s.expire < :date")
                .setParameter("date", ZonedDateTime.now())
                .executeUpdate();
        sqlSession.getTransaction().commit();
        sqlSession.close();
    }

    /**
     * Returns the {@link User} of the {@link Session}
     *
     * @return User of the {@link Session}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets a new {@link User} of the {@link Session}
     *
     * @param user New {@link User} to be set
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Returns the device name of the {@link Session}
     *
     * @return Device name of the {@link Session}
     */
    public String getDeviceName() {
        return this.deviceName;
    }

    /**
     * Sets a new device name of the {@link Session}
     *
     * @param deviceName New device name to be set
     */
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Returns the expire date of the {@link Session}
     *
     * @return Expire date of the {@link Session}
     */
    public ZonedDateTime getExpire() {
        return this.expire;
    }

    /**
     * Sets a new expire date of the {@link Session}
     *
     * @param expire New expire date to be set
     */
    public void setExpire(final ZonedDateTime expire) {
        this.expire = expire;
    }

    /**
     * Returns whether the {@link Session} is valid
     *
     * @return True if the {@link Session} is valid | false if it is not
     */
    public boolean isValid() {
        return ZonedDateTime.now().isBefore(this.getExpire());
    }

    /**
     * Returns the last-active date of the {@link Session}
     *
     * @return Last-active date of the {@link Session}
     */
    public ZonedDateTime getLastActive() {
        return this.lastActive;
    }

    /**
     * Sets a new last-active date of the {@link Session}
     *
     * @param lastActive New last-active date name to be set
     */
    public void setLastActive(final ZonedDateTime lastActive) {
        this.lastActive = lastActive;
    }

    /**
     * Returns the token of the {@link Session}
     *
     * @return Token of the {@link Session}
     */
    public UUID getToken() {
        return this.token;
    }

    /**
     * Sets a new token of the {@link Session}
     *
     * @param token New token to be set
     */
    public void setToken(final UUID token) {
        this.token = token;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Session} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user", this.getId())
                .add("device_name", this.getDeviceName())
                .add("valid", this.isValid())
                .add("expire", this.getExpire())
                .add("last_active", this.getLastActive())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link Session}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Session} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session that = (Session) o;
        return Objects.equals(this.getId(), that.getId()) &&
                Objects.equals(this.getVersion(), that.getVersion()) &&
                Objects.equals(this.getDeviceName(), that.getDeviceName()) &&
                Objects.equals(this.getExpire(), that.getExpire()) &&
                Objects.equals(this.getLastActive(), that.getLastActive()) &&
                Objects.equals(this.getToken(), that.getToken());
    }

    /**
     * Hashes the {@link Session} using {@link Objects} hash method
     *
     * @return Hash of the {@link Session}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getVersion(), this.getDeviceName(), this.getExpire(),
                this.getLastActive(), this.isValid(), this.getToken());
    }
}
