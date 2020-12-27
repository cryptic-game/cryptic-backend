package net.cryptic_game.backend.base.api;

import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.context.ContextHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ComponentScan
public class ApiService {

    private final ContextHandler context;
    private Set<ApiEndpointCollectionData> collections;

    public ApiService(final ContextHandler context) {
        this.context = context;
    }

    @PostConstruct
    private void postConstruct() {
        this.collections = ApiEndpointCollectionParser.parseCollections(
                this.context.getBeansWithAnnotation(ApiEndpointCollection.class), this.context::getBean);
    }

    public final Set<ApiEndpointCollectionData> getCollections(final ApiType type) {
        return this.collections.parallelStream()
                .filter(collection -> collection.getType().equals(type) || collection.getType().equals(ApiType.ALL))
                .collect(Collectors.toUnmodifiableSet());
    }
}
