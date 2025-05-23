package dev.datlag.mimasu.ui.navigation.login.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import dev.datlag.mimasu.composeapp.generated.resources.Res
import dev.datlag.mimasu.composeapp.generated.resources.login_agreement
import dev.datlag.mimasu.composeapp.generated.resources.login_privacy_policy
import dev.datlag.mimasu.composeapp.generated.resources.login_terms_of_service
import dev.datlag.mimasu.core.Constants
import dev.datlag.tooling.compose.platform.PlatformText
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
fun LoginAgreement(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val rawAgreement = stringResource(Res.string.login_agreement)
    val terms = stringResource(Res.string.login_terms_of_service)
    val privacy = stringResource(Res.string.login_privacy_policy)
    val agreement = remember(rawAgreement, terms, privacy) {
        buildAnnotatedString {
            val termsIndex = rawAgreement.indexOf("{terms}")
            val termsAgreement = rawAgreement.replace("{terms}", terms)
            val privacyIndex = termsAgreement.indexOf("{privacy}")
            val agreement = termsAgreement.replace("{privacy}", privacy)

            append(agreement)

            if (termsIndex >= 0) {
                addLink(clickable = LinkAnnotation.Clickable(
                    tag = "TERMS",
                    styles = TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline)),
                    linkInteractionListener = object : LinkInteractionListener {
                        override fun onClick(link: LinkAnnotation) {
                            uriHandler.openUri(Constants.TERMS_CONDITIONS)
                        }
                    }
                ), start = termsIndex, end = min(termsIndex + terms.length, agreement.length))
            }

            if (privacyIndex >= 0) {
                addLink(clickable = LinkAnnotation.Clickable(
                    tag = "PRIVACY",
                    styles = TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline)),
                    linkInteractionListener = object : LinkInteractionListener {
                        override fun onClick(link: LinkAnnotation) {
                            uriHandler.openUri(Constants.PRIVACY_POLICY)
                        }
                    }
                ), start = privacyIndex, end = min(privacyIndex + privacy.length, agreement.length))
            }
        }
    }

    PlatformText(
        modifier = modifier,
        text = agreement,
        textAlign = TextAlign.Center
    )
}