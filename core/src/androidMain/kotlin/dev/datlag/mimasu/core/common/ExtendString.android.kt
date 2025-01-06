package dev.datlag.mimasu.core.common

import dev.datlag.mimasu.core.Sprintf
import dev.datlag.tooling.scopeCatching
import kotlinx.collections.immutable.toImmutableList

/**
 * Good old C-sprintf. See formats table in the
 * [ReadMe](https://github.com/sergeych/mp_stools/blob/master/README.md#printf--sprintf)
 */
actual fun String.sprintf(vararg args: Any): String = scopeCatching {
    Sprintf(this, args.toImmutableList()).process().toString()
}.getOrNull() ?: this.format(*args)