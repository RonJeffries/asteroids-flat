package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2

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
)

fun draw(
    spaceObject: SpaceObject,
    drawer: Drawer,
) {
    drawer.isolated {
        drawer.translate(spaceObject.x, spaceObject.y)
        drawer.scale(4.00, 4.0)
        drawer.rotate(spaceObject.angle)
        drawer.stroke = ColorRGBa.WHITE
        drawer.lineStrip(spaceObject.type.points)
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