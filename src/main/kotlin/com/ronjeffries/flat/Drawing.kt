package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import kotlin.math.max
import kotlin.random.Random

val rocks = listOf(
    listOf(
        Vector2(4.0, 2.0), Vector2(3.0, 0.0), Vector2(4.0, -2.0),
        Vector2(1.0, -4.0), Vector2(-2.0, -4.0), Vector2(-4.0, -2.0),
        Vector2(-4.0, 2.0), Vector2(-2.0, 4.0), Vector2(0.0, 2.0),
        Vector2(2.0, 4.0), Vector2(4.0, 2.0),
    ),
    listOf(
        Vector2(2.0, 1.0), Vector2(4.0, 2.0), Vector2(2.0, 4.0),
        Vector2(0.0, 3.0), Vector2(-2.0, 4.0), Vector2(-4.0, 2.0),
        Vector2(-3.0, 0.0), Vector2(-4.0, -2.0), Vector2(-2.0, -4.0),
        Vector2(-1.0, -3.0), Vector2(2.0, -4.0), Vector2(4.0, -1.0),
        Vector2(2.0, 1.0)
    ),
    listOf(
        Vector2(-2.0, 0.0), Vector2(-4.0, -1.0), Vector2(-2.0, -4.0),
        Vector2(0.0, -1.0), Vector2(0.0, -4.0), Vector2(2.0, -4.0),
        Vector2(4.0, -1.0), Vector2(4.0, 1.0), Vector2(2.0, 4.0),
        Vector2(-1.0, 4.0), Vector2(-4.0, 1.0), Vector2(-2.0, 0.0)
    ),
    listOf(
        Vector2(1.0, 0.0), Vector2(4.0, 1.0), Vector2(4.0, 2.0),
        Vector2(1.0, 4.0), Vector2(-2.0, 4.0), Vector2(-1.0, 2.0),
        Vector2(-4.0, 2.0), Vector2(-4.0, -1.0), Vector2(-2.0, -4.0),
        Vector2(1.0, -3.0), Vector2(2.0, -4.0), Vector2(4.0, -2.0),
        Vector2(1.0, 0.0)
    )
)

val asteroidPoints =
    listOf(
        Vector2(4.0, 2.0), Vector2(3.0, 0.0), Vector2(4.0, -2.0),
        Vector2(1.0, -4.0), Vector2(-2.0, -4.0), Vector2(-4.0, -2.0),
        Vector2(-4.0, 2.0), Vector2(-2.0, 4.0), Vector2(0.0, 2.0),
        Vector2(2.0, 4.0), Vector2(4.0, 2.0),
    )

val missilePoints =
    listOf(
        Vector2(-1.0, -1.0), Vector2(-1.0, 1.0), Vector2(1.0, 1.0),
        Vector2(1.0, -1.0), Vector2(-1.0, -1.0)
    )

val saucerPoints = listOf(
    Vector2(-2.0, -1.0), Vector2(2.0, -1.0), Vector2(5.0, 1.0),
    Vector2(-5.0, 1.0), Vector2(-2.0, 3.0), Vector2(2.0, 3.0),
    Vector2(5.0, 1.0), Vector2(2.0, -1.0), Vector2(1.0, -3.0),
    Vector2(-1.0, -3.0), Vector2(-2.0, -1.0), Vector2(-5.0, 1.0),
    Vector2(-2.0, -1.0)
)

val shipPoints = listOf(
    Vector2(-3.0, -2.0), Vector2(-3.0, 2.0), Vector2(-5.0, 4.0),
    Vector2(7.0, 0.0), Vector2(-5.0, -4.0), Vector2(-3.0, -2.0)
)

val shipFlare = listOf(
    Vector2(-3.0,-2.0), Vector2(-7.0,0.0), Vector2(-3.0, 2.0)
)

fun draw(spaceObject: SpaceObject, drawer: Drawer, deltaTime: Double) {
    drawer.isolated {
        val scale = 4.0 *spaceObject.scale
        drawer.translate(spaceObject.x, spaceObject.y)
        drawer.scale(scale, scale)
        drawer.rotate(spaceObject.angle)
        drawer.stroke = ColorRGBa.WHITE
        drawer.strokeWeight = 1.0/scale
        shipSpecialHandling(spaceObject, drawer, deltaTime)
        spaceObject.type.draw(drawer, spaceObject)
    }
}

private fun shipSpecialHandling(spaceObject: SpaceObject, drawer: Drawer, deltaTime: Double) {
    if (spaceObject.type == SpaceObjectType.SHIP) {
        dropScale = max(dropScale - U.ShipDropInScale*deltaTime, 1.0)
        drawer.scale(dropScale, dropScale)
        if (Controls.accelerate && Random.nextInt(1, 3) == 1) {
            drawer.lineStrip(shipFlare)
        }
    }
}