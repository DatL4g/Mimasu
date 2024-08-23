package dev.datlag.mimasu.firebase

import android.content.Context
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun Context.initializeFirebase(
    projectId: String?,
    applicationId: String,
    apiKey: String,
) = Firebase.initialize(
    context = this,
    options = FirebaseOptions(
        projectId = projectId,
        applicationId = applicationId,
        apiKey = apiKey
    )
)