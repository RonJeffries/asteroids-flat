package com.ronjeffries.flat

import org.openrndr.draw.Drawer


fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    for (so in spaceObjects) {
        move(so, width, height, deltaTime)
    }
    for (so in spaceObjects) {
        draw(so, drawer)
    }
}