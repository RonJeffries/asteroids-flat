package com.ronjeffries.flat

interface Component { val entity: SpaceObject }

data class Timer(override val entity: SpaceObject, val startTime: Double): Component {
    var time = startTime
}

data class SaucerTimer(override val entity: SpaceObject): Component {
    var time = U.SaucerDelay
}

class PluggableTimer(
    override val entity: SpaceObject,
    private val startTime: Double,
    val action: (PluggableTimer) -> Unit
): Component {
    var time = startTime
    fun update(deltaTime: Double) {
        time -= deltaTime
        if (time <= 0.0 ) {
            action(this)
            time = startTime
        }
    }
}

class ActionTimer(
    override val entity: SpaceObject,
    val delayTime: Double,
    val action: (ActionTimer) -> Unit
): Component {
    var time = delayTime
    fun update(deltaTime: Double) {
        if ( ! entity.active ) return
        time -= deltaTime
        if ( time <= 0.0) {
            action(this)
            time = delayTime
        }
    }

}