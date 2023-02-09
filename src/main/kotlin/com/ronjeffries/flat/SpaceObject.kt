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

enum class SpaceObjectType {
    ASTEROID, SHIP
}

val points: Map<SpaceObjectType, List<Vector2>> = mapOf(
    SpaceObjectType.ASTEROID to asteroidPoints,
    SpaceObjectType.SHIP to shipPoints
)

data class SpaceObject(
    val type: SpaceObjectType,
    var x: Double,
    var y: Double,
    var dx: Double,
    var dy: Double,
    var angle: Double = 0.0,
)

fun draw(
    spaceObject: SpaceObject,
    drawer: Drawer,
) {
    val points = points[spaceObject.type] ?: return
    drawer.isolated {
        drawer.translate(spaceObject.x, spaceObject.y)
        drawer.scale(4.00, 4.0)
        drawer.rotate(spaceObject.angle)
        drawer.stroke = ColorRGBa.WHITE
        drawer.lineStrip(points)
    }
}

fun move(spaceObject: SpaceObject, width: Double, height: Double, deltaTime: Double) {
    with (spaceObject) {
        x += dx*deltaTime
        if ( x > width ) x -= width
        if ( x < 0 ) x += width
        y += dy*deltaTime
        if ( y > height ) y -= width
        if ( y < 0 ) y += width
    }
}