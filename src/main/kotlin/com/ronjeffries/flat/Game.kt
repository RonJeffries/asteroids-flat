package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import org.openrndr.math.asDegrees
import java.lang.Integer.min
import kotlin.math.atan2
import kotlin.math.max
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
    const val AsteroidKillRadius = 16.0
    const val AsteroidSpeed = 100.0
    const val AsteroidWaveDelay = 4.0
    const val LightSpeed = 500.0
    const val MissileCount = 6
    const val MissileKillRadius = 1.0
    const val MissileOffset = 50.0
    const val MissileSpeed = LightSpeed / 3.0
    const val MissileTime = 3.0
    const val SaucerDelay = 7.0
    const val SaucerSpeed = 150.0
    const val ScreenHeight = 1024
    const val ScreenWidth = 1024
    const val ShipDelay = 4.0
    const val ShipDecelerationFactor = 0.5
    const val ShipDropInScale = 3.0
    const val ShipKillRadius = 24.0
    const val ShipDeltaV = 120.0
}

var Score: Int = 0
lateinit var SpaceObjects: Array<SpaceObject>
lateinit var Ship: SpaceObject
lateinit var Saucer: SpaceObject

fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    updateEverything(spaceObjects, deltaTime, width, height)
    drawEverything(spaceObjects, drawer)
    checkCollisions()
    drawScore(drawer)
    checkIfShipNeeded(deltaTime)
    checkIfSaucerNeeded(deltaTime)
    checkIfAsteroidsNeeded(deltaTime)
}

private fun updateEverything(
    spaceObjects: Array<SpaceObject>,
    deltaTime: Double,
    width: Int,
    height: Int
) {
    for (spaceObject in spaceObjects) {
        for (component in spaceObject.components) update(component, deltaTime)
        if (spaceObject.type == SpaceObjectType.SHIP) applyControls(spaceObject, deltaTime)
        move(spaceObject, width, height, deltaTime)
    }
}

private fun drawEverything(spaceObjects: Array<SpaceObject>, drawer: Drawer) {
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer)
    }
}

var AsteroidsGoneFor = 0.0
fun checkIfAsteroidsNeeded(deltaTime: Double) {
    if (activeAsteroids(SpaceObjects).isEmpty()) {
        AsteroidsGoneFor += deltaTime
        if (AsteroidsGoneFor > U.AsteroidWaveDelay) {
            AsteroidsGoneFor = 0.0
            activateAsteroids(nextWaveSize(currentWaveSize))
        }
    }
}

private var currentWaveSize = 0
fun nextWaveSize(previousSize: Int): Int = min(previousSize +2,11)

var dropScale = U.ShipDropInScale
private var shipGoneFor = 0.0
fun checkIfShipNeeded(deltaTime: Double) {
    if ( ! Ship.active ) {
        shipGoneFor += deltaTime
        if (shipGoneFor > U.ShipDelay) {
            dropScale = U.ShipDropInScale
            Ship.position = Vector2(U.ScreenWidth/2.0, U.ScreenHeight/2.0)
            Ship.velocity = Vector2(0.0,0.0)
            Ship.angle = 0.0
            Ship.active = true
            shipGoneFor = 0.0
        }
    } else {
        dropScale = max(dropScale - U.ShipDropInScale*deltaTime, 1.0)
    }
}

private var saucerSpeed = U.SaucerSpeed
private var saucerGoneFor = 0.0
fun checkIfSaucerNeeded(deltaTime: Double) {
    saucerGoneFor += deltaTime
    if (saucerGoneFor > U.SaucerDelay) {
        saucerGoneFor = 0.0
        if (!Saucer.active) {
            Saucer.active = true
            Saucer.position = Vector2(0.0, Random.nextDouble(U.ScreenHeight.toDouble()))
            Saucer.velocity = Vector2(saucerSpeed, 0.0)
            saucerSpeed *= -1.0
        } else {
            Saucer.active = false
        }
    }
}

private fun drawScore(drawer: Drawer) {
    val charSpace = 30.0
    val lineSpace = 64.0
    drawer.isolated {
        translate(charSpace/2, lineSpace)
        scale(4.0, 4.0)
        stroke = ColorRGBa.GREEN
        fill = ColorRGBa.GREEN
        text(formatted(), Vector2(0.0, 0.0))
    }
}

fun formatted(): String = ("00000" + Score.toShort()).takeLast(5)

private fun checkCollisions() {
    checkAllMissilesVsAsteroids()
    if ( Ship.active ) checkShipVsAsteroids(Ship)
}

private fun checkAllMissilesVsAsteroids() {
    for (missile in activeMissiles(SpaceObjects)) {
        checkMissileVsAsteroids(missile)
    }
}

private fun checkMissileVsAsteroids(missile: SpaceObject) {
    for (asteroid in activeAsteroids(SpaceObjects)) {
        checkOneAsteroid(asteroid, missile, U.MissileKillRadius)
    }
}

private fun checkShipVsAsteroids(ship: SpaceObject) {
    for (asteroid in activeAsteroids(SpaceObjects)) {
        checkOneAsteroid(asteroid, ship, U.ShipKillRadius)
    }
}

fun checkOneAsteroid(asteroid: SpaceObject, collider: SpaceObject, colliderKillRadius: Double) {
    if (colliding(asteroid, collider,colliderKillRadius)) {
        Score += getScore(asteroid,collider)
        splitOrKillAsteroid(asteroid)
        deactivate(collider)
    }
}

fun colliding(asteroid: SpaceObject, collider: SpaceObject, colliderSize: Double): Boolean {
    val asteroidSize = U.AsteroidKillRadius * asteroid.scale
    return collider.position.distanceTo(asteroid.position) <= asteroidSize + colliderSize
}

private fun splitOrKillAsteroid(asteroid: SpaceObject) {
    if (asteroid.scale > 1) {
        activateAsteroid(asteroid, asteroid.scale / 2, asteroid.position, randomVelocity())
        spawnNewAsteroid(asteroid)
    } else deactivate(asteroid)
}

private fun getScore(asteroid: SpaceObject, collider: SpaceObject): Int {
    if (collider.type != SpaceObjectType.MISSILE) return 0
    return when (asteroid.scale) {
        4.0 -> 20
        2.0 -> 50
        1.0 -> 100
        else -> 0
    }
}

private fun spawnNewAsteroid(asteroid: SpaceObject) {
    val newOne: SpaceObject? = SpaceObjects.firstOrNull { it.type == SpaceObjectType.ASTEROID && ! it.active }
    if (newOne != null) {
        val newVelocity = asteroid.velocity.rotate(Random.nextDouble(90.0,270.0))
        activateAsteroid(newOne, asteroid.scale, asteroid.position, newVelocity)
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

private fun spaceFrictionPerSecond(vNew: Vector2, vCurrent: Vector2): Vector2 {
    return vNew - vCurrent
}

fun applyControls(spaceObject: SpaceObject, deltaTime: Double) {
    if (Controls.left) spaceObject.angle -= 250.0 * deltaTime
    if (Controls.right) spaceObject.angle += 250.0 * deltaTime
    if (Controls.accelerate) {
        incrementVelocity(spaceObject, Vector2(U.ShipDeltaV, 0.0).rotate(spaceObject.angle) * deltaTime)
    } else {
        val deceleration
            = spaceFrictionPerSecond(spaceObject.velocity*U.ShipDecelerationFactor, spaceObject.velocity)*deltaTime
        Ship.velocity += deceleration
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
        if (!SpaceObjects[i].active) return action(SpaceObjects[i])
    }
}

fun createGame(missileCount: Int, asteroidCount: Int) {
    Score = 0
    val objects = mutableListOf<SpaceObject>()
    for (i in 1..missileCount) objects.add(newMissile())
    Ship = newShip()
    objects.add(Ship)
    Saucer = newSaucer()
    objects.add(Saucer)
    for (i in 1..asteroidCount) objects.add(newAsteroid())
    SpaceObjects = objects.toTypedArray()
}

fun startGame(width: Int, height: Int) {
    Ship.active = true
    Ship.position = Vector2(width/2.0, height/2.0)
    activateAsteroids(4)
}

private fun activateAsteroids(asteroidCount: Int) {
    deactivateAsteroids()
    currentWaveSize = asteroidCount
    for (i in 1..asteroidCount) activateAsteroidAtEdge()
}

private fun deactivateAsteroids() {
    SpaceObjects.filter { it.type == SpaceObjectType.ASTEROID }.forEach { deactivate(it) }
}

fun activateAsteroidAtEdge() {
    val asteroids = SpaceObjects.filter { it.type == SpaceObjectType.ASTEROID }
    val available = asteroids.firstOrNull { !it.active }
    if (available != null) {
        val edgePosition = Vector2(0.0, Random.nextDouble(U.ScreenHeight.toDouble()))
        activateAsteroid(available, 4.0, edgePosition, randomVelocity())
    }
}

private fun activateAsteroid(asteroid: SpaceObject, scale: Double, position: Vector2, velocity: Vector2) {
    asteroid.position = position
    asteroid.scale = scale
    asteroid.velocity = velocity
    asteroid.angle = randomAngle()
    asteroid.active = true
}

private fun randomVelocity(): Vector2 {
    return Vector2(U.AsteroidSpeed, 0.0).rotate(randomAngle())
}

private fun randomAngle() = Random.nextDouble(360.0)

fun newMissile(): SpaceObject {
    return SpaceObject(SpaceObjectType.MISSILE, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        .also { addComponent(it, Timer(it, 3.0)) }
}

private fun newSaucer(): SpaceObject = SpaceObject(SpaceObjectType.SAUCER, 0.0, 0.0, 0.0, 0.0, 0.0, false)

private fun newShip(): SpaceObject = SpaceObject(SpaceObjectType.SHIP, 0.0, 0.0, 0.0, 0.0, 0.0, false)

fun newAsteroid(): SpaceObject = SpaceObject(SpaceObjectType.ASTEROID, 0.0, 0.0, 0.0, 0.0, 0.0, false)
    .also { it.scale = 4.0 }
