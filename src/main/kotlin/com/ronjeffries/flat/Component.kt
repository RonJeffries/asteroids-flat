package com.ronjeffries.flat

interface Component { val entity: SpaceObject }

data class SaucerTimer(override val entity: SpaceObject): Component {
    var time = U.SaucerDelay
}

class Timer(
    override val entity: SpaceObject,
    val delayTime: Double,
    val processWhenActive: Boolean,
    var extra: (Timer) -> Boolean,
    val action: (Timer) -> Unit
): Component {
    constructor(
        entity: SpaceObject,
        delayTime: Double,
        action: (Timer) -> Unit
    ): this(entity, delayTime, true, { true }, action)
    constructor(
        entity: SpaceObject,
        delayTime: Double,
        processWhenActive: Boolean,
        action: (Timer) -> Unit
    ): this(entity, delayTime, processWhenActive, {true}, action)

    var time = delayTime
}

