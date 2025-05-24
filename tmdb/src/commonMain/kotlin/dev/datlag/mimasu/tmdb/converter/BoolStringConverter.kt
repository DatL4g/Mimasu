package dev.datlag.mimasu.tmdb.converter

import de.jensklingenberg.ktorfit.converter.Converter
import dev.datlag.mimasu.core.typeOf
import kotlin.reflect.KClass

data object BoolStringConverter : Converter.Factory {

    private fun asStringBoolean(value: String): Boolean = when {
        value.equals("true", ignoreCase = true) -> true
        value == "1" -> true
        else -> value.toBoolean()
    }

    fun asBoolean(any: Any): Boolean = when {
        any is Boolean -> any
        else -> asStringBoolean(value = any.toString())
    }

    fun asString(value: Boolean): String = if (value) {
        "true"
    } else {
        "false"
    }

    fun asInt(value: Boolean): Int = if (value) {
        1
    } else {
        0
    }

    override fun requestParameterConverter(
        parameterType: KClass<*>,
        requestType: KClass<*>
    ): Converter.RequestParameterConverter? {
        val toBoolean = parameterType typeOf String::class && requestType typeOf Boolean::class

        return object : Converter.RequestParameterConverter {
            override fun convert(data: Any): Any {
                return if (toBoolean) {
                    asBoolean(data)
                } else {
                    asString(asBoolean(data))
                }
            }
        }
    }
}