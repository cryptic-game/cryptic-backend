package net.cryptic_game.backend.base.api.executor;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Slf4j
final class ApiEndpointExecutor {

    private ApiEndpointExecutor() {
        throw new UnsupportedOperationException();
    }

    static Mono<ApiResponse> execute(final ApiRequest request, final ApiEndpointData endpoint) {
        if (endpoint.isDisabled()) {
            return Mono.just(new ApiResponse(HttpResponseStatus.SERVICE_UNAVAILABLE, "ENDPOINT_DISABLED"));
        }

        if (!endpoint.getAuthenticator().isPermitted(request, endpoint.getAuthentication(), endpoint)) {
            return Mono.just(new ApiResponse(HttpResponseStatus.UNAUTHORIZED));
        }

        request.setEndpointData(endpoint);

        final Object[] parameters;

        try {
            parameters = ApiParameterExecutor.parseParameters(request, endpoint.getParameters());
        } catch (ApiParameterException e) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, e.getMessage()));
        } catch (Throwable e) {
            log.error("Unable to parse parameters of endpoint {}.", getEndpointIdentifier(endpoint), e);
            return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }

        try {
            final Object response = endpoint.getMethod().invoke(endpoint.getInstance(), parameters);

            if (response == null) {
                log.error("Endpoint {} returned null, which is not allowed.", getEndpointIdentifier(endpoint));
                return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
            }

            if (response instanceof ApiResponse) {
                return Mono.just((ApiResponse) response);
            } else if (response instanceof Mono) {
                return ((Mono<?>) response).cast(ApiResponse.class);
            } else {
                throw new IllegalStateException(String.format("Neither a %s nor a %s was returned by endpoint %s.",
                        ApiResponse.class.getName(), Mono.class.getName(), getEndpointIdentifier(endpoint)));
            }

        } catch (IllegalArgumentException e) {
            log.error("Endpoint {} does not have the correct parameters.", getEndpointIdentifier(endpoint), e);
        } catch (InvocationTargetException e) {
            log.error("An exception was thrown in endpoint {}.", getEndpointIdentifier(endpoint), e.getTargetException());
        } catch (Throwable e) {
            log.error("Unable to execute endpoint {}.", getEndpointIdentifier(endpoint), e);
        }

        return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }

    private static String getEndpointIdentifier(final ApiEndpointData endpoint) {
        return endpoint.getClazz() == null ? endpoint.getId() : String.format("%s.%s", endpoint.getClazz().getName(), endpoint.getMethod().getName());
    }
}
