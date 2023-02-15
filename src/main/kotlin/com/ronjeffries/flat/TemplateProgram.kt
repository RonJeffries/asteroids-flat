package com.ronjeffries.flat

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont

fun main() = application {
    configure {
        title = "Asteroids"
        width = Width
        height = Height
    }

    program {
//        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)
        createGame(6,26)
        startGame()
        var lastTime = 0.0
        var deltaTime = 0.0
        keyboard.keyDown.listen {
            when (it.name) {
                "d" -> {controls_left = true}
                "f" -> {controls_right = true}
                "j" -> {controls_accelerate = true}
                "k" -> {controls_fire = true}
                "space" -> {controls_hyperspace = true}
//                "q" -> { insertQuarter()}
            }
        }
        keyboard.keyUp.listen {
            when (it.name) {
                "d" -> {controls_left = false}
                "f" -> {controls_right = false}
                "j" -> {controls_accelerate = false}
                "k" -> {controls_fire = false}
                "space" -> {
                    controls_hyperspace = false
                }
            }
        }

        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.RED
            deltaTime = seconds - lastTime
            lastTime = seconds
            gameCycle(spaceObjects,width,height,drawer, deltaTime)
        }
    }
}

