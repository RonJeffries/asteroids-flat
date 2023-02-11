package com.ronjeffries.flat

import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont

var controls_left: Boolean = false
var controls_right: Boolean = false
var controls_accelerate: Boolean = false
var controls_fire: Boolean = false
var controls_hyperspace: Boolean = false

fun main() = application {
    configure {
        width = 512
        height = 512
    }

    program {
//        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/default.otf", 64.0)
        val ship = SpaceObject(
            SpaceObjectType.SHIP,
            width/2.0,
            height/2.0,
            0.0,
            0.0,
            0.0,
        )
        val asteroid = SpaceObject(
            SpaceObjectType.ASTEROID,
            300.0,
            300.0,
            74.0,
            40.0,
        )
        val spaceObjects = arrayOf(asteroid, ship)
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

