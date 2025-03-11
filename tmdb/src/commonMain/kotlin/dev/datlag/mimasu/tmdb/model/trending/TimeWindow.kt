package dev.datlag.mimasu.tmdb.model.trending

import kotlin.reflect.KClass
import de.jensklingenberg.ktorfit.converter.Converter as Konverter

sealed interface TimeWindow {

    val value: String

    data object Day : TimeWindow {
        override val value: String = "day"

        override fun toString(): String {
            return value
        }
    }

    data object Week : TimeWindow {
        override val value: String = "week"

        override fun toString(): String {
            return value
        }
    }

    companion object Converter : Konverter.Factory {
        override fun requestParameterConverter(
            parameterType: KClass<*>,
            requestType: KClass<*>
        ): Konverter.RequestParameterConverter {
            return object : Konverter.RequestParameterConverter {
                override fun convert(data: Any): Any {
                    return (data as? TimeWindow)?.value ?: data.toString().ifBlank { null } ?: data
                }
            }
        }
    }
}