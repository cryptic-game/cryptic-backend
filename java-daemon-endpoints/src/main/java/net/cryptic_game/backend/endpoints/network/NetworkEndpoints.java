package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network");
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter("user_id") final UUID userId,
                           @ApiParameter(value = "network_id", optional = true) final UUID network_id,
                           @ApiParameter(value = "name", optional = true) final String name) {
        if (network_id == null && name == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NO_NAME_OR_NETWORK_ID_PROVIDED");
        }

        final Network network = network_id == null ? Network.getByName(name) : Network.getById(network_id);
        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("public")
    public ApiResponse getPublic() {
        return new ApiResponse(ApiResponseType.OK, Network.getPublicNetworks());
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("device_id") final UUID deviceId,
                              @ApiParameter("name") final String name,
                              @ApiParameter("hidden") final Boolean hidden) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (Network.getNetworksOwnedByDevice(device).size() >= 2) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");
        }

        if (Network.getByName(name) != null) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");
        }

        final Network network = Network.createNetwork(name, device, hidden);
        NetworkMember.createMember(network, device);
        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("members")
    public ApiResponse members(@ApiParameter("network_id") final UUID network_id) {
        final Network network = Network.getById(network_id);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfNetwork(network));
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter("user_id") final UUID userId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfDevice(device));
    }
}
