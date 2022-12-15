package coffee.cypher.gradleutil.filters

import blue.endless.jankson.*
import java.io.Reader

object JsonUtil {
    private val jankson: Jankson = Jankson.builder().build()

    fun Reader.asJson() = jankson.load(this.readText().replace("\r\n", "\n"))

    fun JsonElement.flatten(): JsonElement = if (this is JsonObject) {
        val newObj = JsonObject()

        this.flatMap { (k, v) ->
            val newValue = v.flatten()
            val newPrefix = if (k.isEmpty() || k.last() in ":_-/")
                k
            else
                "$k."

            if (newValue is JsonObject)
                newValue.entries.map { (key, value) ->
                    val newKey = if (key.isEmpty()) {
                        k
                    } else {
                        "$newPrefix$key"
                    }
                    newKey to value
                }
            else
                listOf(k to newValue)
        }.forEach { (k, v) ->
            newObj.put(k, v, null)
        }

        newObj
    } else {
        this
    }

    fun JsonElement.removeIndents(): JsonElement = when (this) {
        is JsonPrimitive -> {
            val value = this.value

            if (value is String) {
                JsonPrimitive(value.replace(Regex("\\|\\s+"), ""))
            } else {
                this
            }
        }
        is JsonArray -> {
            mapTo(JsonArray()) { it.removeIndents() }
        }
        is JsonObject -> {
            this.entries.map { it.key to it.value.removeIndents() }.associateTo(JsonObject()) { it }
        }
        else -> this
    }

    fun JsonElement.asStandardType(): Any? = when (this) {
        is JsonObject -> this.mapValues { (_, v) -> v.asStandardType() }
        is JsonArray -> this.map { it.asStandardType() }
        is JsonPrimitive -> this.value
        is JsonNull -> null

        else -> throw IllegalArgumentException("Unknown JSON element: $this")
    }
}
