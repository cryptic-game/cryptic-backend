package net.cryptic_game.backend.admin.authentication;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.handler.rest.RestApiAuthenticator;
import net.cryptic_game.backend.base.api.handler.rest.RestApiRequest;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.Group;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
@RequiredArgsConstructor
public final class AdminPanelAuthenticator implements RestApiAuthenticator {

    private final Key key;

    @Override
    public boolean isPermitted(final RestApiRequest request, final int authentication, final ApiEndpointData endpoint) {
        if (authentication == 0) return true;

        final String jwt = request.getContext().getHttpRequest().requestHeaders().get(HttpHeaderNames.AUTHORIZATION);
        if (jwt == null || jwt.isBlank()) return false;

        final JsonObject jwtJson;
        try {
            jwtJson = SecurityUtils.parseJwt(this.key, jwt);
            if (jwtJson.has("refresh_token")) return false;
        } catch (SignatureException | ExpiredJwtException | JsonParseException e) {
            return false;
        } catch (Throwable e) {
            log.error("Error while validating jwt token.");
            return false;
        }

        request.getContext().set(jwtJson);

        for (final JsonElement groupId : JsonUtils.fromJson(jwtJson.get("groups"), JsonArray.class)) {
            final Group group = Group.byId(groupId.getAsString());
            if (group != null) {
                for (final int permission : group.getPermissions()) {
                    if (authentication == permission) return true;
                }
            }
        }

        return false;
    }
}
