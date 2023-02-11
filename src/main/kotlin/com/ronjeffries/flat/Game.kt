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
        if (spaceObject.type == SpaceObjectType.SHIP) {
            if ( controls_left ) spaceObject.angle -= 250.0*deltaTime
            if ( controls_right ) spaceObject.angle += 250.0*deltaTime
            if (controls_accelerate) {
                spaceObject.dy += 120.0*deltaTime
            }
        }
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        draw(spaceObject, drawer)
    }
}