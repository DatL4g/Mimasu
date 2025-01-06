package dev.datlag.mimasu.core.common

/**
 * Good old C-sprintf. See formats table in the
 * [ReadMe](https://github.com/sergeych/mp_stools/blob/master/README.md#printf--sprintf)
 */
expect fun String.sprintf(vararg args: Any): String