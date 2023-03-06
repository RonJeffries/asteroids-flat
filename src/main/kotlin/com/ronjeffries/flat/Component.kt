package com.ronjeffries.flat

interface Component { val entity: SpaceObject }

data class SaucerTimer(override val entity: SpaceObject): Component {
    var time = U.SaucerDelay
}

class Timer(
    override val entity: SpaceObject,
    val delayTime: Double,
    val processWhenActive: Boolean = true,
    val action: (Timer) -> Unit
): Component {
    var time = delayTime
    fun update(deltaTime: Double) {
        if ( entity.active == processWhenActive ) {
            time -= deltaTime
            if (time <= 0.0) {
                action(this)
                time = delayTime
            }
        }
    }
}

