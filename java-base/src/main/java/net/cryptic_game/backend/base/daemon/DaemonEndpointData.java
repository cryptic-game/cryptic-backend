package net.cryptic_game.backend.base.daemon;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;

import java.lang.reflect.Method;

@Getter
@Setter
@EqualsAndHashCode
public final class DaemonEndpointData extends ApiEndpointData {

    private Daemon daemon;

    public DaemonEndpointData(final String id, final String description, final int authentication,
                              final boolean disabled, final ApiAuthenticator authenticator, final Object instance, final Class<?> clazz,
                              final Method method) {
        super(description, authentication, clazz, disabled, authenticator, id, null, instance, method);
    }
}
