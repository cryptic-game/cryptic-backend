package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonArray;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.daemon.DaemonRegisterPacket;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonInfoEndpoints extends ApiEndpointCollection {

    private final static Logger log = LoggerFactory.getLogger(DaemonInfoEndpoints.class);
    private final DaemonHandler daemonHandler;

    public DaemonInfoEndpoints(final DaemonHandler daemonHandler) {
        super("daemon");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("register")
    public ApiResponse register(
            final ApiClient client,
            @ApiParameter("name") final String name,
            @ApiParameter("functions") final JsonArray functions) {

        final DaemonRegisterPacket drp = new DaemonRegisterPacket(client.getChannel(), name, functions);
        this.daemonHandler.addDaemon(drp.getDaemon());

        log.info("Daemon \"" + name + "\" registered.");

        return new ApiResponse(ApiResponseType.OK);
    }
}
