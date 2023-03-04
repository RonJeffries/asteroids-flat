package com.ronjeffries.flat

interface Component { val entity: SpaceObject }

data class Timer(override val entity: SpaceObject, val startTime: Double): Component {
    var time = startTime
}

data class SaucerTimer(override val entity: SpaceObject): Component {
    var time = U.SaucerDelay
}