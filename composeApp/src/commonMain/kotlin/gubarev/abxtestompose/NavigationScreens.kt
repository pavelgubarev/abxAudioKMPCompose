package gubarev.abxtestompose

import kotlinx.serialization.Serializable

@Serializable
object Loader

@Serializable
data class Testing(val tracksFiles: Map<TrackCode, String> = mapOf())
