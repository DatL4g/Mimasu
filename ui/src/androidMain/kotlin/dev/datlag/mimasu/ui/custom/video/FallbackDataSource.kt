package dev.datlag.mimasu.ui.custom.video

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.HttpDataSource
import dev.datlag.tooling.scopeCatching

@UnstableApi
class FallbackDataSource(
    private vararg val _dataSources: DataSource?
) : BaseDataSource(_dataSources.any { it is HttpDataSource }) {

    private var currentDataSource: DataSource? = null
    private val dataSources = _dataSources.filterNotNull()

    override fun open(dataSpec: DataSpec): Long {
        var lastException: Throwable? = null
        for (source in dataSources) {
            try {
                return source.open(dataSpec).also {
                    currentDataSource = source
                }
            } catch (e: Throwable) {
                lastException = e
                scopeCatching {
                    source.close()
                }
            }
        }

        throw lastException ?: IllegalStateException("Iterated through all DataSources and failed but no Exception thrown")
    }

    override fun getUri(): Uri? {
        return currentDataSource?.uri ?: dataSources.firstNotNullOfOrNull { it.uri }
    }

    override fun close() {
        try {
            currentDataSource?.close()
        } finally {
            currentDataSource = null
        }
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return currentDataSource?.responseHeaders ?: super.getResponseHeaders()
    }

    override fun read(
        buffer: ByteArray,
        offset: Int,
        length: Int
    ): Int {
        val source = currentDataSource ?: throw IllegalStateException("DataSource not opened or all fallbacks failed.")

        return source.read(buffer, offset, length)
    }
}