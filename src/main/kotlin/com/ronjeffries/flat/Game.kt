package com.ronjeffries.flat

import org.openrndr.draw.Drawer
import org.openrndr.math.Vector2
import kotlin.random.Random

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
    const val AsteroidSpeed = 100.0
    const val LightSpeed = 500.0
    const val MissileCount = 6
    const val MissileOffset = 50.0
    const val MissileSpeed = LightSpeed / 3.0
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
        for (component in spaceObject.components) update(component, deltaTime)
        if (spaceObject.type == SpaceObjectType.SHIP) applyControls(spaceObject, deltaTime)
        move(spaceObject, width, height, deltaTime)
    }
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer)
    }
    checkCollisions()
}

private fun checkCollisions() {
    checkAllMissiles()
}

private fun checkAllMissiles() {
    for (missile in activeMissiles(spaceObjects)) {
        checkAllAsteroids(missile)
    }
}

private fun checkAllAsteroids(missile: SpaceObject) {
    for (asteroid in activeAsteroids(spaceObjects)) {
        checkOneAsteroid(asteroid, missile)
    }
}

private fun checkOneAsteroid(
    asteroid: SpaceObject,
    missile: SpaceObject
) {
    val killDist = 16.0 * asteroid.scale + 1
    val dist = missile.position.distanceTo(asteroid.position)
    if (dist <= killDist) {
        if (asteroid.scale > 1) {
            asteroid.scale /= 2
            asteroid.velocity = randomVelocity()
            spawnNewAsteroid(asteroid)
        } else deactivate(asteroid)
        deactivate(missile)
    }
}

private fun spawnNewAsteroid(asteroid: SpaceObject) {
    val newOne: SpaceObject? = spaceObjects.firstOrNull { it.type == SpaceObjectType.ASTEROID && !it.active }
    if (newOne != null) {
        newOne.position = asteroid.position
        newOne.scale = asteroid.scale
        newOne.active = true
        val angle = Random.nextDouble(90.0, 270.0)
        newOne.velocity = asteroid.velocity.rotate(angle)
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
        if (!entity.active) return
        time -= deltaTime
        if (time <= 0.0) {
            deactivate(entity)
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
    spaceObject.velocity = spaceObject.velocity + deltaV
}

fun fireMissile() {
    Controls.fire = false
    withAvailableMissile { missile ->
        missile.position = Ship.position + Vector2(U.MissileOffset, 0.0).rotate(Ship.angle)
        setVelocityRelativeToShip(missile, Vector2(U.MissileSpeed, 0.0).rotate(Ship.angle))
        missile.active = true
    }
}

private fun setVelocityRelativeToShip(spaceObject: SpaceObject, velocity: Vector2) {
    spaceObject.velocity = velocity + Vector2(Ship.dx, Ship.dy)
}

fun withAvailableMissile(action: (SpaceObject) -> Unit) {
    for (i in 2..5) {
        if (!spaceObjects[i].active) return action(spaceObjects[i])
    }
}

fun createGame(missileCount: Int, asteroidCount: Int) {
    val objects = mutableListOf<SpaceObject>()
    for (i in 1..missileCount) objects.add(newMissile())
    Ship = newShip()
    objects.add(Ship)
    for (i in 1..asteroidCount) objects.add(newAsteroid())
    spaceObjects = objects.toTypedArray()
}

fun startGame(width: Int, height: Int) {
    Ship.active = true
    Ship.position = Vector2(width/2.0, height/2.0)
    activateAsteroids(4)
}

private fun activateAsteroids(asteroidCount: Int) {
    deactivateAsteroids()
    for (i in 1..asteroidCount) activateAsteroidAtEdge()
}

private fun deactivateAsteroids() {
    spaceObjects.filter { it.type == SpaceObjectType.ASTEROID }.forEach { deactivate(it) }
}

fun activateAsteroidAtEdge() {
    val asteroids = spaceObjects.filter { it.type == SpaceObjectType.ASTEROID }
    val available = asteroids.firstOrNull { !it.active }
    if (available != null) {
        available.active = true
        available.position = Vector2(0.0, Random.nextDouble(U.ScreenHeight.toDouble()))
        available.velocity = randomVelocity()
    }
}

private fun randomVelocity(): Vector2 {
    val randomAngle = Random.nextDouble(360.0)
    return Vector2(U.AsteroidSpeed, 0.0).rotate(randomAngle)
}

private fun newMissile(): SpaceObject {
    return SpaceObject(SpaceObjectType.MISSILE, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        .also { addComponent(it, Timer(it, 3.0)) }
}

private fun newShip(): SpaceObject = SpaceObject(SpaceObjectType.SHIP, 0.0, 0.0, 0.0, 0.0, 0.0, false)

private fun newAsteroid(): SpaceObject = SpaceObject(SpaceObjectType.ASTEROID, 0.0, 0.0, 0.0, 0.0, 0.0, false)
    .also { it.scale = 4.0 }
