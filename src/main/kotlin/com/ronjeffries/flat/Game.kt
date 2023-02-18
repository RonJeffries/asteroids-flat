package com.ronjeffries.flat

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2

// Globals

object Controls {
    var left: Boolean = false
    var right: Boolean = false
    var accelerate: Boolean = false
    var fire: Boolean = false
    var hyperspace: Boolean = false
}

object U {
    const val AsteroidCount = 26
    const val LightSpeed = 500.0
    const val MissileCount = 6
    const val MissileOffset = 50.0
    const val MissileSpeed = LightSpeed/3.0
    const val MissileTime = 3.0
    const val ScreenHeight = 1024
    const val ScreenWidth = 1024
    const val ShipDeltaV = 120.0
}

lateinit var spaceObjects: Array<SpaceObject>
lateinit var Ship: SpaceObject

fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    for (spaceObject in spaceObjects) {
        for (component in spaceObject.components) {
            update(component, deltaTime)
        }
        if (spaceObject.type == SpaceObjectType.SHIP) {
            applyControls(spaceObject, deltaTime)
        }
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer)
    }
}

fun update(component: Component, deltaTime: Double) {
    when (component) {
        is Timer -> {
            updateTimer(component, deltaTime)
        }
    }
}

private fun updateTimer(timer: Timer, deltaTime: Double) {
    with(timer) {
        if ( ! entity.active) return
        time -= deltaTime
        if (time <= 0.0) {
            entity.active = false
            time = timer.startTime
        }
    }
}

private fun applyControls(spaceObject: SpaceObject, deltaTime: Double) {
    if (Controls.left) spaceObject.angle -= 250.0 * deltaTime
    if (Controls.right) spaceObject.angle += 250.0 * deltaTime
    if (Controls.accelerate) {
        incrementVelocity(spaceObject, Vector2(U.ShipDeltaV, 0.0).rotate(spaceObject.angle) * deltaTime)
    }
    if (Controls.fire) fireMissile()
}

private fun incrementVelocity(spaceObject: SpaceObject, deltaV: Vector2) {
    spaceObject.dx += deltaV.x
    spaceObject.dy += deltaV.y
}

fun fireMissile() {
    Controls.fire = false
    withAvailableMissile { missile ->
        setPosition(missile, Vector2(U.MissileOffset, 0.0).rotate(Ship.angle))
        setVelocity(missile, Vector2(U.MissileSpeed, 0.0).rotate(Ship.angle))
        missile.active = true
    }
}

private fun setPosition(missile: SpaceObject, offset: Vector2) {
    missile.x = offset.x + Ship.x
    missile.y = offset.y + Ship.y
}

private fun setVelocity(missile: SpaceObject, velocity: Vector2) {
    missile.dx = velocity.x + Ship.dx
    missile.dy = velocity.y + Ship.dy
}

fun withAvailableMissile(action: (SpaceObject) -> Unit) {
    for ( i in 2..5) {
        if (!spaceObjects[i].active) return action(spaceObjects[i])
    }
}

fun createGame(missileCount: Int, asteroidCount: Int) {
    val objects = mutableListOf<SpaceObject>()
    for ( i in 1..missileCount) {
        objects.add(newMissile())
    }
    Ship = newShip()
    objects.add(Ship)
    spaceObjects = objects.toTypedArray()
}

fun startGame(width: Int, height: Int) {
    Ship.active = true
    Ship.x = width/2.0
    Ship.y = height/2.0
}

private fun newMissile(): SpaceObject {
    return SpaceObject(SpaceObjectType.MISSILE, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        .also { addComponent(it, Timer(it, 3.0)) }
}

private fun newShip(): SpaceObject
    = SpaceObject(SpaceObjectType.SHIP, 0.0, 0.0, 0.0, 0.0, 0.0, false)
