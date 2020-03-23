package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service_service")
public class Service extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "device", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private Device device;

    @Column(name = "name", updatable = true, nullable = true)
    private String name;

    @Column(name = "running", updatable = true, nullable = true, columnDefinition = "TINYINT")
    private boolean running;

    @Column(name = "running_port", updatable = true, nullable = true)
    private int runningPort;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(final Device device) {
        this.device = device;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(final boolean running) {
        this.running = running;
    }

    public int getRunningPort() {
        return this.runningPort;
    }

    public void setRunningPort(final int runningPort) {
        this.runningPort = runningPort;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("device_id", this.getDevice().getId())
                .add("name", this.getName())
                .add("isRunning", this.isRunning())
                .add("runningPort", this.getRunningPort())
                .add("user", this.getUser().getId())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return isRunning() == service.isRunning() &&
                getRunningPort() == service.getRunningPort() &&
                Objects.equals(getDevice(), service.getDevice()) &&
                Objects.equals(getName(), service.getName()) &&
                Objects.equals(getUser(), service.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getName(), isRunning(), getRunningPort(), getUser());
    }
}
