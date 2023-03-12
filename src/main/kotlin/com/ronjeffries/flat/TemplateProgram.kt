package com.ronjeffries.flat

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont

fun main() = application {
    configure {
        title = "Asteroids"
        width = U.ScreenWidth
        height = U.ScreenHeight
    }

    program {
//        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)
        createGame(U.SaucerMissileCount, U.ShipMissileCount, U.AsteroidCount)
        startGame(width, height)
        var lastTime = 0.0
        var deltaTime = 0.0
        keyboard.keyDown.listen {
            when (it.name) {
                "d" -> {Controls.left = true}
                "f" -> {Controls.right = true}
                "j" -> {Controls.accelerate = true}
                "k" -> {Controls.fire = true}
                "space" -> {Controls.hyperspace = true}
//                "q" -> { insertQuarter()}
            }
        }
        keyboard.keyUp.listen {
            when (it.name) {
                "d" -> {Controls.left = false}
                "f" -> {Controls.right = false}
                "j" -> {Controls.accelerate = false}
                "k" -> {Controls.fire = false}
                "space" -> {
                    Controls.hyperspace = false
                }
            }
        }

        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.RED
            deltaTime = seconds - lastTime
            lastTime = seconds
            gameCycle(SpaceObjects,width,height,drawer, deltaTime)
        }
    }
}

