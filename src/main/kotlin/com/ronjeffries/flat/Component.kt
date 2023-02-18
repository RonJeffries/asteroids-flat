package com.ronjeffries.flat

interface Component { val entity: SpaceObject }

data class Timer(override val entity: SpaceObject, val startTime: Double): Component {
    var time = startTime
}