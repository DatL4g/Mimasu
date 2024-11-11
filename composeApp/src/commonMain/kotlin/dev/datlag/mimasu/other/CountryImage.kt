package dev.datlag.mimasu.other

import mimasu.composeapp.generated.resources.Res
import mimasu.composeapp.generated.resources.cn
import mimasu.composeapp.generated.resources.de
import mimasu.composeapp.generated.resources.es
import mimasu.composeapp.generated.resources.fr
import mimasu.composeapp.generated.resources.jp
import mimasu.composeapp.generated.resources.ru
import mimasu.composeapp.generated.resources.us
import org.jetbrains.compose.resources.DrawableResource

data object CountryImage {
    fun getByCode(code: String): DrawableResource? {
        val bestCode = code.split("[-_]".toRegex()).firstOrNull() ?: code

        return when {
            bestCode.equals("cn", ignoreCase = true) -> Res.drawable.cn
            bestCode.equals("de", ignoreCase = true) -> Res.drawable.de
            bestCode.equals("en", ignoreCase = true) || bestCode.equals("us", ignoreCase = true) -> Res.drawable.us
            bestCode.equals("es", ignoreCase = true) -> Res.drawable.es
            bestCode.equals("fr", ignoreCase = true) -> Res.drawable.fr
            bestCode.equals("jp", ignoreCase = true) -> Res.drawable.jp
            bestCode.equals("ru", ignoreCase = true) -> Res.drawable.ru
            else -> null
        }
    }
}