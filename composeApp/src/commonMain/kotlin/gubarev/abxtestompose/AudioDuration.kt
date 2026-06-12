package gubarev.abxtestompose

import kotlin.math.abs

const val MAX_DURATION_DIFF_SECONDS = 1.0

fun durationsMatch(durA: Double?, durB: Double?): Boolean =
    durA != null && durB != null && abs(durA - durB) <= MAX_DURATION_DIFF_SECONDS

expect fun getAudioDuration(path: String): Double?
