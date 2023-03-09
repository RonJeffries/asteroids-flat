package com.ronjeffries.flat

import org.openrndr.math.Vector2

val asteroidRadius = { asteroid: SpaceObject -> U.AsteroidKillRadius* asteroid.scale}
val missileRadius = { _: SpaceObject -> U.MissileKillRadius }
val saucerRadius = { _: SpaceObject -> U.SaucerKilLRadius }
val shipRadius = { _: SpaceObject -> U.ShipKillRadius }

fun killRadius(spaceObject: SpaceObject) = spaceObject.type.killRadius(spaceObject)

enum class SpaceObjectType(val points: List<Vector2>, val killRadius: (SpaceObject)->Double) {
    ASTEROID(asteroidPoints, asteroidRadius),
    SHIP(shipPoints, shipRadius),
    SAUCER(saucerPoints, saucerRadius),
    MISSILE(missilePoints, missileRadius)
}

data class SpaceObject(
    val type: SpaceObjectType,
    var x: Double,
    var y: Double,
    var dx: Double,
    var dy: Double,
    var angle: Double = 0.0,
    var active: Boolean = false,
) {
    var position: Vector2
        get() = Vector2(x,y)
        set(v) {
            x = v.x
            y = v.y
        }
    var velocity: Vector2
        get() = Vector2(dx,dy)
        set(v) {
            dx = v.x
            dy = v.y
        }
    var scale = 1.0
    var components: MutableList<Component> = mutableListOf()
}

fun activeAsteroids(spaceObjects: Array<SpaceObject>): List<SpaceObject> {
    return spaceObjects.filter {it.type == SpaceObjectType.ASTEROID && it.active}
}

fun activeMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> {
    return missiles(spaceObjects).filter { it.active}
}

fun missiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> {
    return spaceObjects.filter {it.type == SpaceObjectType.MISSILE}
}

fun addComponent(entity: SpaceObject, component: Component) {
    if (component is Timer ) TimerTable += component
    else entity.components.add(component)
}

fun deactivate(entity: SpaceObject) {
    entity.active = false
    for (component in entity.components) {
        when (component) {
            is SaucerTimer -> {
                component.time = U.SaucerDelay
            }
        }
    }
    for (timer in TimerTable) {
        if (timer.entity == entity ) timer.time = timer.delayTime
    }
}


fun move(spaceObject: SpaceObject, width: Double, height: Double, deltaTime: Double) {
    with (spaceObject) {
        x = limit(x+dx*deltaTime, width)
        y = limit(y+dy*deltaTime, height)
    }
}

private fun limit(value: Double, max: Double): Double {
    var result = value
    while (result < 0) result += max
    while (result > max) result -= max
    return result
}