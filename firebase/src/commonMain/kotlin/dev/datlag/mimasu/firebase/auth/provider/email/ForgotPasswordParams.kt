package dev.datlag.mimasu.firebase.auth.provider.email

data class ForgotPasswordParams(
    val email: String,
    val url: String,
    val iosParams: IOSParams? = null,
    val androidParams: AndroidParams? = null,
    val canHandleCodeInApp: Boolean
) {
    data class IOSParams(
        val iOSBundleId: String
    )

    data class AndroidParams(
        val androidPackageName: String,
        val installIfNotAvailable: Boolean = true,
        val minimumVersion: String? = null
    )
}