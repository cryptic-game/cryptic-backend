package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpoint;

public class SendEndpoint extends DaemonEndpoint {

    public SendEndpoint() {
        super("send");
    }

    @Override
    public JsonObject handleRequest(JsonObject data) throws Exception {
        return null;
    }
}
