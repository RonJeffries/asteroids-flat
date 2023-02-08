package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2

private val asteroidPoints =
    listOf(
        Vector2(4.0, 2.0), Vector2(3.0, 0.0), Vector2(4.0, -2.0),
        Vector2(1.0, -4.0), Vector2(-2.0, -4.0), Vector2(-4.0, -2.0),
        Vector2(-4.0, 2.0), Vector2(-2.0, 4.0), Vector2(0.0, 2.0),
        Vector2(2.0, 4.0), Vector2(4.0, 2.0),
    )

data class Asteroid(var x: Double, var y: Double, var dx: Double, var dy: Double)

fun drawAsteroid(asteroid: Asteroid, drawer: Drawer) {
    drawer.isolated {
        drawer.translate(asteroid.x, asteroid.y)
        drawer.scale(4.00, 4.0)
        drawer.stroke = ColorRGBa.WHITE
        drawer.lineStrip(asteroidPoints)
    }
}

fun moveAsteroid(asteroid: Asteroid, width: Double, height: Double, deltaTime: Double) {
    with (asteroid) {
        x += dx*deltaTime
        if ( x > width ) x -= width
        if ( x < 0 ) x += width
        y += dy*deltaTime
        if ( y > height ) y -= width
        if ( y < 0 ) y += width
    }
}