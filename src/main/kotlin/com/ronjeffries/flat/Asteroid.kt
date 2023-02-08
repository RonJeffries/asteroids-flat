package com.ronjeffries.flat

data class Asteroid(var x: Double, var y: Double, var dx: Double, var dy: Double)

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