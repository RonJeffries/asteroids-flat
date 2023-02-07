package com.ronjeffries.flat

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
    configure {
        width = 512
        height = 512
    }

    program {
//        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)
        val asteroid = Asteroid(512.0, 512.0, 100.0, -90.0)
        var lastTime = 0.0
        var deltaTime = 0.0

        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.RED
            deltaTime = seconds - lastTime
            lastTime = seconds
            moveAsteroid(asteroid, width + 0.0, height + 0.0, deltaTime)
            with (asteroid) {
                x += dx*deltaTime
                if ( x > width) x -= width
                if ( y > height ) y -= height
                if ( x < 0 ) x += width
                if ( y < 0 ) y += width
                y += dy*deltaTime
            }

            drawer.circle(asteroid.x, asteroid.y, 32.0)

            drawer.fontMap = font
            drawer.fill = ColorRGBa.WHITE
            drawer.text("Asteroids™", width / 2.0, height / 2.0)
        }
    }
}
