package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class JsonUtils {

    /**
     * Returns the associated {@link String} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link String} value of the property.
     */
    public static String getString(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsString();
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link Integer} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link Integer} value of the property.
     */
    public static int getInt(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsInt();
        } else {
            return -1;
        }
    }

    /**
     * Returns the associated {@link Long} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link Long} value of the property.
     */
    public static long getLong(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsLong();
        } else {
            return -1L;
        }
    }

    /**
     * Returns the associated {@link Boolean} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link Boolean} value of the property.
     */
    public static boolean getBoolean(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsBoolean();
        } else {
            return false;
        }
    }

    /**
     * Returns the associated {@link UUID} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link UUID} value of the property.
     */
    public static UUID getUUID(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            try {
                return UUID.fromString(jsonObject.get(key).getAsString());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link Date} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link Date} value of the property.
     * @deprecated Usage of {@link LocalDate} and {@link LocalDateTime} is preferred.
     * Use {@link JsonUtils#getLocalDate(JsonObject, String)} and {@link JsonUtils#getLocalDateTime(JsonObject, String)} instead.
     */
    @Deprecated
    public static Date getDate(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return new Date(jsonObject.get(key).getAsLong());
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link LocalDate} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link LocalDate} value of the property.
     */
    public static LocalDate getLocalDate(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return LocalDate.parse(jsonObject.get(key).getAsString());
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link LocalDateTime} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link LocalDateTime} value of the property.
     */
    public static LocalDateTime getLocalDateTime(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return LocalDateTime.parse(jsonObject.get(key).getAsString());
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link JsonObject} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link JsonObject} value of the property.
     */
    public static JsonObject getJsonObject(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsJsonObject();
        } else {
            return null;
        }
    }

    /**
     * Returns the associated {@link JsonArray} value of a specific property.
     *
     * @param jsonObject The {@link JsonObject} where the value should be fetched.
     * @param key        The property name.
     * @return The {@link JsonArray} value of the property.
     */
    public static JsonArray getJsonArray(JsonObject jsonObject, String key) {
        if (jsonObject.get(key) != null) {
            return jsonObject.get(key).getAsJsonArray();
        } else {
            return null;
        }
    }

    public static <T extends JsonSerializable> JsonArray toArray(final List<T> items) {
        return toArray(items, JsonSerializable::serialize);
    }

    public static <T> JsonArray toArray(final List<T> items, final Function<T, JsonElement> function) {
        final JsonArray array = new JsonArray();
        items.forEach(item -> array.add(function.apply(item)));
        return array;
    }

    public static <T extends JsonSerializable> JsonArray toArray(final Set<T> items) {
        return toArray(items, JsonSerializable::serialize);
    }

    public static <T> JsonArray toArray(final Set<T> items, final Function<T, JsonElement> function) {
        final JsonArray array = new JsonArray();
        items.forEach(item -> array.add(function.apply(item)));
        return array;
    }
}
