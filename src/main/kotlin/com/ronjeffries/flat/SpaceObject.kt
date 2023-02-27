package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import java.lang.NullPointerException
import kotlin.random.Random

private val shipPoints = listOf(
    Vector2(-3.0, -2.0), Vector2(-3.0, 2.0), Vector2(-5.0, 4.0),
    Vector2(7.0, 0.0), Vector2(-5.0, -4.0), Vector2(-3.0, -2.0)
)

private val shipFlare = listOf(
    Vector2(-3.0,-2.0), Vector2(-7.0,0.0), Vector2(-3.0, 2.0)
)

private val asteroidPoints =
    listOf(
        Vector2(4.0, 2.0), Vector2(3.0, 0.0), Vector2(4.0, -2.0),
        Vector2(1.0, -4.0), Vector2(-2.0, -4.0), Vector2(-4.0, -2.0),
        Vector2(-4.0, 2.0), Vector2(-2.0, 4.0), Vector2(0.0, 2.0),
        Vector2(2.0, 4.0), Vector2(4.0, 2.0),
    )

private val missilePoints =
    listOf(Vector2(-1.0, -1.0), Vector2(-1.0, 1.0), Vector2(1.0, 1.0),
        Vector2(1.0, -1.0), Vector2(-1.0, -1.0)
    )

enum class SpaceObjectType(val points: List<Vector2>) {
    ASTEROID(asteroidPoints),
    SHIP(shipPoints),
    MISSILE(missilePoints)
}

data class SpaceObject(
    val type: SpaceObjectType,
    var x: Double,
    var y: Double,
    var dx: Double,
    var dy: Double,
    var angle: Double = 0.0,
    var active: Boolean = true,
) {
    var position: Vector2
        get() = Vector2(x,y)
        set(v:Vector2) {
            x = v.x
            y = v.y
        }
    var velocity: Vector2
        get() = Vector2(dx,dy)
        set(v:Vector2) {
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
    return spaceObjects.filter {it.type == SpaceObjectType.MISSILE && it.active}
}

fun addComponent(entity: SpaceObject, component: Component) {
    entity.components.add(component)
}

fun deactivate(entity: SpaceObject) {
    entity.active = false
    for (component in entity.components) {
        when (component) {
            is Timer -> {
                component.time = component.startTime
            }
        }
    }
}

fun draw(
    spaceObject: SpaceObject,
    drawer: Drawer,
) {
    drawer.isolated {
        val scale = 4.0 *spaceObject.scale
        drawer.translate(spaceObject.x, spaceObject.y)
        drawer.scale(scale, scale)
        drawer.rotate(spaceObject.angle)
        drawer.stroke = ColorRGBa.WHITE
        drawer.strokeWeight = 1.0/scale
        possiblyDrawFlare(spaceObject, drawer)
        drawer.lineStrip(spaceObject.type.points)
    }
}

private fun possiblyDrawFlare(spaceObject: SpaceObject, drawer: Drawer) {
    if (spaceObject.type == SpaceObjectType.SHIP) {
        if (Controls.accelerate && Random.nextInt(1, 3) == 1) {
            drawer.lineStrip(shipFlare)
        }
    }
}

fun move(spaceObject: SpaceObject, width: Int, height: Int, deltaTime: Double) {
    move(spaceObject, width.toDouble(), height.toDouble(), deltaTime)
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