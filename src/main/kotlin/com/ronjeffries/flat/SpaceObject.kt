package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.draw.Drawer
import kotlin.math.max
import kotlin.random.Random

val asteroidRadius = { asteroid: SpaceObject -> U.AsteroidKillRadius* asteroid.scale}
val missileRadius = { _: SpaceObject -> U.MissileKillRadius }
val saucerRadius = { _: SpaceObject -> U.SaucerKilLRadius }
val shipRadius = { _: SpaceObject -> U.ShipKillRadius }
val splatRadius = { _: SpaceObject -> 0.0}

fun killRadius(spaceObject: SpaceObject) = spaceObject.type.killRadius(spaceObject)

val drawAsteroid = {drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    drawer.lineStrip(rocks[spaceObject.pointsIndex])
}

val drawSaucer = { drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    drawer.lineStrip(saucerPoints)
}

val drawSaucerMissile = { drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    drawer.stroke = ColorRGBa.RED
    drawer.fill = ColorRGBa.RED
    drawer.circle(Vector2.ZERO, spaceObject.type.killRadius(spaceObject))
}

val drawShip = { drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    dropScale = max(dropScale - U.ShipDropInScale*deltaTime, 1.0)
    drawer.scale(dropScale, dropScale)
    drawer.lineStrip(shipPoints)
    if (Controls.accelerate && Random.nextInt(1, 3) == 1) {
        drawer.lineStrip(shipFlare)
    }
}

val drawShipMissile = { drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    drawer.circle(Vector2.ZERO, spaceObject.type.killRadius(spaceObject))
}

var elapsedSplatTime: Double = 0.0
val drawSplat = { drawer: Drawer, spaceObject: SpaceObject, deltaTime: Double ->
    elapsedSplatTime += deltaTime*1.0
    if (elapsedSplatTime > 2.0) {
        spaceObject.active = false
    }
    var splatRadius = 1.0 + elapsedSplatTime*2.0
    drawer.scale(splatRadius, splatRadius)
    val opacity = 1.0*(2.0- elapsedSplatTime)
    val color = ColorRGBa(1.0 ,1.0, 1.0, opacity)
    drawer.fill = color
    drawer.stroke = color
    for (point in splatPoints) {
        drawer.circle(point, 1/(5*splatRadius))
    }
}

enum class SpaceObjectType(
    val killRadius: (SpaceObject) -> Double,
    val draw: (Drawer, SpaceObject, Double) -> Unit
) {
    ASTEROID(asteroidRadius, drawAsteroid),
    SHIP(shipRadius, drawShip),
    SAUCER(saucerRadius, drawSaucer),
    MISSILE(missileRadius, drawShipMissile),
    SAUCER_MISSILE(missileRadius, drawSaucerMissile),
    SPLAT(splatRadius, drawSplat)
}

data class SpaceObject(
    val type: SpaceObjectType,
    var x: Double,
    var y: Double,
    var dx: Double,
    var dy: Double,
    var angle: Double = 0.0,
    var active: Boolean = false,
    var spinRate: Double = 0.0,
    var pointsIndex: Int = 0
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

fun activeShipMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    shipMissiles(spaceObjects).filter { it.active}

fun activeSaucerMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    saucerMissiles(spaceObjects).filter { it.active }

fun saucerMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    spaceObjects.filter { it.type == SpaceObjectType.SAUCER_MISSILE }

fun addComponent(entity: SpaceObject, component: Component) {
    if (component is Timer ) TimerTable += component
    else entity.components.add(component)
}

fun deactivate(entity: SpaceObject) {
    if (splittable(entity)) {
        activateAsteroid(entity, entity.scale / 2, entity.position, randomVelocity())
        spawnNewAsteroid(entity)
    } else {
        entity.active = false
        resetComponents(entity)
    }
    if (entity.type == SpaceObjectType.SHIP) {
        activateSplat(entity,Splat)
    }
}

private fun activateSplat(entity: SpaceObject, splat: SpaceObject) {
    splat.active = true
    elapsedSplatTime = 0.0
    splat.position = entity.position
    splat.angle = randomAngle()
}

private fun splittable(entity: SpaceObject): Boolean {
    return entity.type == SpaceObjectType.ASTEROID && entity.scale > 1
}

private fun resetComponents(entity: SpaceObject) {
    for (component in entity.components) {
        when (component) {
            is SaucerTimer -> component.time = U.SaucerDelay
        }
    }
    for (timer in TimerTable) {
        if (timer.entity == entity) timer.time = timer.delayTime
    }
}

fun shipMissiles(spaceObjects: Array<SpaceObject>): List<SpaceObject> =
    spaceObjects.filter {it.type == SpaceObjectType.MISSILE}

fun move(spaceObject: SpaceObject, width: Double, height: Double, deltaTime: Double) {
    with (spaceObject) {
        angle += spinRate*deltaTime
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