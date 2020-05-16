package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApiEndpointList {

    private final String hostname;
    private final int port;
    private final Map<String, ApiEndpointCollectionData> collections;
    private final Map<String, ApiEndpointData> endpoints;
    private final Set<ApiClient> clients;

    private byte[] playgroundContent;

    public ApiEndpointList(final String hostname, final int port) {
        this.hostname = hostname;
        this.port = port;
        this.collections = new HashMap<>();
        this.endpoints = new HashMap<>();
        this.clients = new HashSet<>();
        this.playgroundContent = new byte[0];
    }

    public Map<String, ApiEndpointCollectionData> getCollections() {
        return this.collections;
    }

    public void setCollections(final Set<ApiEndpointCollection> apiCollections) throws ApiException {
        for (final ApiEndpointCollection collection : apiCollections) {
            this.collections.put(collection.getName(), ApiParser.parseEndpointCollection(collection));
            if (collection.hasClient()) {
                throw new ApiException("Endpoint-Collection \"" + collection.getName() + "\" is already register.");
            }
            collection.setClients(this.clients);
        }
        this.collections.forEach((name, collectionData) -> collectionData.getEndpoints().forEach((endpointName, endpointData) -> this.endpoints.merge(endpointName, endpointData, (mergeName, mergeEndpointData) -> mergeEndpointData)));
        if (this.hostname != null)
            this.playgroundContent = ApiParser.toPlayground(this.hostname + ":" + this.port, this.collections.values()).toString().getBytes(StandardCharsets.UTF_8);
    }

    public void addCollections(final Collection<ApiEndpointCollectionData> endpointCollections) {
        endpointCollections.forEach(collection -> {
            this.collections.put(collection.getName(), collection);
            collection.getEndpoints().forEach((endpointName, endpointData) -> this.endpoints.merge(endpointName, endpointData, (mergeName, mergeEndpointData) -> mergeEndpointData));
        });
        if (this.hostname != null) {
            this.playgroundContent = ApiParser.toPlayground(this.hostname + ":" + this.port, this.collections.values()).toString().getBytes(StandardCharsets.UTF_8);
        }
    }

    public void remove(final ApiEndpointCollectionData endpointCollection) {
        this.collections.remove(endpointCollection.getName());
        endpointCollection.getEndpoints().forEach((name, endpoint) -> this.endpoints.remove(name));
    }

    public Map<String, ApiEndpointData> getEndpoints() {
        return this.endpoints;
    }

    public Set<ApiClient> getClients() {
        return this.clients;
    }

    public byte[] getPlaygroundContent() {
        return this.playgroundContent;
    }
}
