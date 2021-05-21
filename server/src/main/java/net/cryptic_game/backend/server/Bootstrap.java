package net.cryptic_game.backend.server;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.DefaultApiAuthenticator;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiInitializer;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiRequest;
import net.cryptic_game.backend.data.sql.repositories.server_management.DisabledEndpointRepository;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.websocket.WebSocketDaemonEndpoints;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Slf4j
@SpringBootApplication(scanBasePackages = "net.cryptic_game.backend")
public class Bootstrap {

//    public Bootstrap() {
//        if (bootstrap.isDebug())
//            httpServerCodec.addLocationProvider("playground", new PlaygroundLocationProvider(config.getWebsocketAddress(),
//                    webSocketEndpointHandler.getApiList().getCollections().values()));
//    }

    public static void main(final String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    @Lazy
    DaemonHandler daemonHandler(
            final WebsocketApiInitializer wsInitializer,
            final BaseConfig config,
            final ApplicationContext context,
            final DisabledEndpointRepository disabledEndpointRepository,
            final DefaultApiAuthenticator authenticator
    ) {
        return new DaemonHandler(wsInitializer.getEndpoints(), config.getApiToken(), context, disabledEndpointRepository, authenticator);
    }

    @Bean
    CommandLineRunner runner(
            final WebsocketApiInitializer wsInitializer,
            final DisabledEndpointRepository disabledEndpointRepository,
            final DaemonHandler daemonHandler
    ) {
        return args -> {
            // disabling endpoints that are disabled in database
            wsInitializer.getEndpoints()
                    .forEach((name, endpoint) -> {
                        if (disabledEndpointRepository.existsById(name)) {
                            endpoint.setDisabled(true);
                        }
                    });

            try {
                daemonHandler.setSend(new WebSocketDaemonEndpoints(),
                        WebSocketDaemonEndpoints.class.getDeclaredMethod("send", WebsocketApiRequest.class));
            } catch (NoSuchMethodException e) {
                log.error("Error while setting daemon endpoint handling method.", e);
            }

            System.getenv()
                    .entrySet()
                    .parallelStream()
                    .filter(entry -> entry.getKey().startsWith("DAEMON_"))
                    .forEach(entry -> daemonHandler.registerDaemon(entry.getKey().substring(7).replace('_', '-').toLowerCase(), entry.getValue()));
        };
    }
}
