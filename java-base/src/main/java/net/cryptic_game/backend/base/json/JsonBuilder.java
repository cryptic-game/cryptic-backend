package net.cryptic_game.backend.base.json;

import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * The {@link JsonBuilder} is a tool to create {@link JsonObject}
 * or a Json as a {@link String} without declaring any variables.
 * <br>
 * Example:
 *
 * <pre>{@code
 * final JsonObject json = JsonBuilder
 *    .create("name", "Max")
 *    .add("age", 43)
 *    .build();
 * }</pre>
 * or
 * <pre>{@code
 * final String json = JsonBuilder
 *    .create("name", "Max")
 *    .add("age", 43)
 *    .toString();
 * }</pre>
 *
 * @see JsonSerializable
 * @see JsonObject
 */
@EqualsAndHashCode
@Slf4j
public final class JsonBuilder implements JsonSerializable {

    private final JsonObject json;

    /**
     * This creates a new instance of {@link JsonBuilder}.
     *
     * @param json the existing {@link JsonObject}
     */
    private JsonBuilder(final JsonObject json) {
        this.json = json;
    }

    /**
     * Creates a {@link JsonBuilder} without any data.
     *
     * @return the JsonBuilder
     * @see JsonBuilder#create(String, Object)
     * @deprecated use {@link JsonBuilder#create(String, Object)}
     */
    @Deprecated
    public static JsonBuilder create() {
        return create(new JsonObject());
    }

    /**
     * Creates a {@link JsonBuilder} without any data.
     *
     * @param json the existing {@link JsonObject}
     * @return the JsonBuilder
     * @see JsonBuilder#create(String, Object)
     */
    public static JsonBuilder create(final JsonObject json) {
        return new JsonBuilder(json);
    }

    /**
     * Creates a {@link JsonBuilder} without any data.
     *
     * @param object the data, represented in a {@link JsonObject}
     * @return the JsonBuilder
     * @see JsonBuilder#create(String, Object)
     */
    public static JsonBuilder create(final Object object) {
        return new JsonBuilder(JsonUtils.fromJson(JsonUtils.toJson(object), JsonObject.class));
    }

    /**
     * Creates a {@link JsonBuilder} with one Key, Value pair.
     *
     * @param key   the key of the pair
     * @param value the value of the pair
     * @return the JsonBuilder
     */
    public static JsonBuilder create(final String key, final Object value) {
        return JsonBuilder.create().add(key, value);
    }

    /**
     * Creates a {@link JsonBuilder} with one Key, Value pair.
     *
     * @param key        the key of the pair
     * @param value      the value of the pair
     * @param value2Call a alternative value if @{code value} is {@code null}
     * @return the JsonBuilder
     */
    public static JsonBuilder create(final String key, final Object value, final Callable<Object> value2Call) {
        return JsonBuilder.create().add(key, value, value2Call);
    }

    /**
     * Creates a {@link JsonBuilder} with one Key, Value pair.
     *
     * @param key       the key of the pair
     * @param condition witch value should be added
     * @param valueCall the value of the pair
     * @return the JsonBuilder
     */
    public static JsonBuilder create(final String key, final boolean condition, final Callable<Object> valueCall) {
        return JsonBuilder.create().add(key, condition, valueCall);
    }

    /**
     * Creates a {@link JsonBuilder} with one Key, Value pair.
     *
     * @param key        the key of the pair
     * @param condition  if the value should be added
     * @param value1Call the value of the pair
     * @param value2Call the alternative value of the pair
     * @return the JsonBuilder
     */
    public static JsonBuilder create(final String key, final boolean condition, final Callable<Object> value1Call, final Callable<Object> value2Call) {
        return JsonBuilder.create().add(key, condition, value1Call, value2Call);
    }

    /**
     * This adds a Key, Value pair to the Json,
     * and return this instance to call other
     * functions to manipulate the Json.
     *
     * @param key   the key of the pair
     * @param value the value of the pair
     * @return a instance if this to call other functions without saving {@link JsonBuilder} to a variable
     */
    public JsonBuilder add(final String key, final Object value) {
        this.json.add(key, JsonUtils.toJson(value));
        return this;
    }

    public JsonBuilder add(final String key, final Object value, final Callable<Object> value2Call) {
        try {
            return this.add(key, value != null ? value : value2Call.call());
        } catch (Exception e) {
            log.error("Error while calling second value.", e);
        }
        return this;
    }

    public JsonBuilder add(final String key, final boolean condition, final Callable<Object> valueCall) {
        if (condition) try {
            return this.add(key, valueCall.call());
        } catch (Exception e) {
            log.error("Error while calling value.", e);
        }
        return this;
    }

    public JsonBuilder add(final String key, final boolean condition, final Callable<Object> value1Call, final Callable<Object> value2Call) {
        try {
            return this.add(key, condition ? value1Call.call() : value2Call.call());
        } catch (Exception e) {
            log.error("Error while calling second value.", e);
        }
        return this;
    }

    /**
     * Converts this to an {@link JsonObject}.
     *
     * @return an {@link JsonObject} with the values from this
     * @see JsonBuilder#toString()
     */
    public JsonObject build() {
        return this.json;
    }

    /**
     * Converts this to an {@link JsonObject}.
     *
     * @return an {@link JsonObject} with the values from this
     * @see JsonBuilder#build()
     * @deprecated use {@link JsonBuilder#build()}
     */
    @Override
    @Deprecated
    public JsonObject serialize() {
        return this.build();
    }

    /**
     * Converts this to a Json-{@link String}.
     *
     * @return a Json-{@link String} with the values from this
     * @see JsonObject
     * @see JsonBuilder#build()
     */
    @Override
    public String toString() {
        return this.build().toString();
    }
}
