package com.ronjeffries.flat

import org.openrndr.draw.Drawer


fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    for (spaceObject in spaceObjects) {
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        draw(spaceObject, drawer)
    }
}