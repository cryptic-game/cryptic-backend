package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonContentHandler extends NettyHandler<JsonObject> {

    private final static Logger log = LoggerFactory.getLogger(DaemonContentHandler.class);

    private final DaemonHandler daemonHandler;

    public DaemonContentHandler(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final JsonObject msg) throws Exception {
        log.info(msg.toString());
        ctx.write(msg);
    }
}
