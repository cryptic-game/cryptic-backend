package net.cryptic_game.backend.server.server.daemon;

import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public final class DaemonEndpointHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DaemonEndpointHandler.class);
    private final ApiEndpointList apiList;
    private Set<ApiEndpointCollection> apiCollections;

    public DaemonEndpointHandler() {
        this.apiCollections = new HashSet<>();
        this.apiList = new ApiEndpointList(null);
    }

    public void postInit() {
        try {
            this.apiList.setCollections(this.apiCollections);
            this.apiCollections = null;
        } catch (ApiException e) {
            LOG.error("Unable to register Api-Collections.", e);
        }
    }

    public <T extends ApiEndpointCollection> T addApiCollection(final T apiCollection) {
        if (this.apiCollections != null) {
            this.apiCollections.add(apiCollection);
            return apiCollection;
        } else {
            LOG.error("It's too late to register any more endpoints.");
            return null;
        }
    }

    public ApiEndpointList getApiList() {
        return this.apiList;
    }
}
