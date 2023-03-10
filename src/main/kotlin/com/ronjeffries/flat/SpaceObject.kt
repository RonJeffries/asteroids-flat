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
    MISSILE(missilePoints, missileRadius),
    SAUCER_MISSILE(missilePoints, missileRadius)
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

fun asteroids(spaceObjects: Array<SpaceObject>) = spaceObjects.filter { it.type == SpaceObjectType.ASTEROID }

fun activeAsteroids(spaceObjects: Array<SpaceObject>): List<SpaceObject> = asteroids(spaceObjects).filter {it.active}

fun inactiveAsteroids(spaceObjects: Array<SpaceObject>): List<SpaceObject> = asteroids(spaceObjects).filter { ! it.active}

fun activeMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    missiles(spaceObjects).filter { it.active}

fun activeSaucerMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    saucerMissiles(spaceObjects).filter { it.active }

fun saucerMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    spaceObjects.filter { it.type == SpaceObjectType.SAUCER_MISSILE }

fun addComponent(entity: SpaceObject, component: Component) {
    if (component is Timer ) TimerTable += component
    else entity.components.add(component)
}

fun deactivate(entity: SpaceObject) {
    entity.active = false
    for (component in entity.components) {
        when (component) {
            is SaucerTimer -> component.time = U.SaucerDelay
        }
    }
    for (timer in TimerTable) {
        if (timer.entity == entity ) timer.time = timer.delayTime
    }
}

fun missiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    spaceObjects.filter {it.type == SpaceObjectType.MISSILE}

fun move(spaceObject: SpaceObject, width: Double, height: Double, deltaTime: Double) {
    with (spaceObject) {
        x = wrap(x+dx*deltaTime, width)
        y = wrap(y+dy*deltaTime, height)
    }
}

private fun wrap(value: Double, max: Double): Double {
    var result = value
    while (result < 0) result += max
    while (result > max) result -= max
    return result
}