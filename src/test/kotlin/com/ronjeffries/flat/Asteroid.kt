package com.ronjeffries.flat

data class Asteroid(var x: Double, var y: Double, val dx: Double, val dy: Double)

fun moveAsteroid(asteroid: Asteroid, deltaTime: Double) {
    with (asteroid) {
        x += dx*deltaTime
        y += dy*deltaTime
    }
}