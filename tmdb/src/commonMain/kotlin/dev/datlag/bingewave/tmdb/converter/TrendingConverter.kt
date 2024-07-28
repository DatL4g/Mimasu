package dev.datlag.bingewave.tmdb.converter

import de.jensklingenberg.ktorfit.converter.Converter
import dev.datlag.bingewave.tmdb.model.TrendingWindow
import dev.datlag.tooling.safeCast
import kotlin.reflect.KClass

class TrendingConverter : Converter.Factory {
    override fun requestParameterConverter(
        parameterType: KClass<*>,
        requestType: KClass<*>
    ): Converter.RequestParameterConverter? {
        val parameterIsWindow = TrendingWindow.isTypeOf(parameterType)
        val requestIsString = requestType == String::class

        return if (parameterIsWindow && requestIsString) {
            object : Converter.RequestParameterConverter {
                override fun convert(data: Any): Any {
                    return data.safeCast<TrendingWindow>()?.value ?: data.toString()
                }
            }
        } else {
            super.requestParameterConverter(parameterType, requestType)
        }
    }
}