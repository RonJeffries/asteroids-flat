package com.ronjeffries.flat

import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.isolated
import org.openrndr.math.Vector2
import java.lang.Integer.min
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
    val MissileOffsetFromShip = Vector2(50.0, 0.0)
    val MissileVelocity = Vector2(LightSpeed / 3.0, 0.0)
    const val MissileTime = 3.0
    const val SaucerDelay = 7.0
    const val SaucerKilLRadius = 20.0
    const val SaucerScore = 200
    const val SaucerSpeed = 150.0
    const val ScreenHeight = 1024
    const val ScreenWidth = 1024
          val CenterOfUniverse = Vector2(ScreenWidth/2.0, ScreenHeight/2.0)
    const val SafeShipDistance = ScreenHeight/10.0
    const val ShipDelay = 4.0
    const val ShipDecelerationFactor = 0.5
    const val ShipDropInScale = 3.0
    const val ShipKillRadius = 24.0
    const val ShipDeltaV = 120.0
}

var Score: Int = 0
var AsteroidsGoneFor = 0.0
private var currentWaveSize = 0
var dropScale = U.ShipDropInScale
lateinit var Saucer: SpaceObject
var saucerSpeed = U.SaucerSpeed
lateinit var Ship: SpaceObject
lateinit var SpaceObjects: Array<SpaceObject>
var TimerTable: List<Timer> = mutableListOf<Timer>()

fun gameCycle(
    spaceObjects: Array<SpaceObject>,
    width: Int,
    height: Int,
    drawer: Drawer,
    deltaTime: Double
) {
    updateEverything(spaceObjects, deltaTime, width, height)
    drawEverything(spaceObjects, drawer, deltaTime)
    checkCollisions()
    drawScore(drawer)
    checkIfAsteroidsNeeded(deltaTime)
}

private fun updateEverything(
    spaceObjects: Array<SpaceObject>,
    deltaTime: Double,
    width: Int,
    height: Int
) {
    updateTimers(deltaTime)
    for (spaceObject in spaceObjects) {
        for (component in spaceObject.components) update(component, deltaTime)
        if (spaceObject.type == SpaceObjectType.SHIP) applyControls(spaceObject, deltaTime)
        move(spaceObject, width.toDouble(), height.toDouble(), deltaTime)
    }
}

fun updateTimers(deltaTime: Double) {
    for (timer in TimerTable) {
        updateTimer(timer, deltaTime)
    }
}

fun updateTimer(timer: Timer, deltaTime: Double) {
    with(timer) {
        if (entity.active == processWhenActive) {
            time -= deltaTime
            if (time <= 0.0 && extra(this)) {
                action(this)
                time = delayTime
            }
        }
    }
}

private fun drawEverything(spaceObjects: Array<SpaceObject>, drawer: Drawer, deltaTime: Double) {
    for (spaceObject in spaceObjects) {
        if (spaceObject.active) draw(spaceObject, drawer, deltaTime)
    }
}

fun checkIfAsteroidsNeeded(deltaTime: Double) {
    if (activeAsteroids(SpaceObjects).isEmpty()) {
        AsteroidsGoneFor += deltaTime
        if (AsteroidsGoneFor > U.AsteroidWaveDelay) {
            AsteroidsGoneFor = 0.0
            activateAsteroids(nextWaveSize(currentWaveSize))
        }
    }
}

fun nextWaveSize(previousSize: Int): Int = min(previousSize +2,11)

fun activateShip() {
    dropScale = U.ShipDropInScale
    Ship.position = Vector2(U.ScreenWidth / 2.0, U.ScreenHeight / 2.0)
    Ship.velocity = Vector2(0.0, 0.0)
    Ship.angle = 0.0
    Ship.active = true
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
    if ( Saucer.active ) checkAllMissilesVsSaucer(Saucer)
}

private fun checkAllMissilesVsSaucer(saucer: SpaceObject) {
    for (missile: SpaceObject in activeMissiles(SpaceObjects)) {
        checkSaucerVsMissile(saucer, missile)
    }
}

fun checkSaucerVsMissile(saucer: SpaceObject, missile: SpaceObject) {
    if (colliding(saucer, missile)) {
        Score += U.SaucerScore
        deactivate(saucer)
        deactivate(missile)
    }
}

private fun checkAllMissilesVsAsteroids() {
    for (missile in activeMissiles(SpaceObjects)) {
        checkMissileVsAsteroids(missile)
    }
}

private fun checkMissileVsAsteroids(missile: SpaceObject) {
    for (asteroid in activeAsteroids(SpaceObjects)) {
        checkOneAsteroid(asteroid, missile)
    }
}

private fun checkShipVsAsteroids(ship: SpaceObject) {
    for (asteroid in activeAsteroids(SpaceObjects)) {
        checkOneAsteroid(asteroid, ship)
    }
}

fun checkOneAsteroid(asteroid: SpaceObject, collider: SpaceObject) {
    if (colliding(asteroid, collider)) {
        Score += getScore(asteroid,collider)
        splitOrKillAsteroid(asteroid)
        deactivate(collider)
    }
}

fun colliding(target: SpaceObject, collider: SpaceObject): Boolean {
    return collider.position.distanceTo(target.position) <= killRadius((target)) + killRadius(collider)
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
        is SaucerTimer -> {
            updateSaucerTimer(component, deltaTime)
        }
    }
}

fun updateSaucerTimer(timer: SaucerTimer, deltaTime: Double) {
    with(timer) {
        time -= deltaTime
        if (time <= 0.0) {
            time = U.SaucerDelay
            if (entity.active) {
                entity.active = false
            } else {
                activateSaucer(entity)
            }
        }
    }
}

fun activateSaucer(entity: SpaceObject) {
    entity.active = true
    entity.position = Vector2(0.0, Random.nextDouble(U.ScreenHeight.toDouble()))
    entity.velocity = Vector2(saucerSpeed, 0.0)
    saucerSpeed *= -1.0
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
        missile.position = Ship.position + U.MissileOffsetFromShip.rotate(Ship.angle)
        missile.velocity = Ship.velocity + U.MissileVelocity.rotate(Ship.angle)
        missile.active = true
    }
}

fun withAvailableMissile(action: (SpaceObject) -> Unit) {
    for (i in 2..5) {
        if (!SpaceObjects[i].active) return action(SpaceObjects[i])
    }
}

fun startGame(width: Int, height: Int) {
    saucerSpeed = U.SaucerSpeed
    Ship.active = true
    Ship.position = Vector2(width/2.0, height/2.0)
    Saucer.active = false
    activateAsteroids(4)
}

fun activateAsteroids(asteroidCount: Int) {
    deactivateAsteroids()
    currentWaveSize = asteroidCount
    for (i in 1..asteroidCount) activateAsteroidAtEdge()
}

private fun deactivateAsteroids() = activeAsteroids(SpaceObjects).forEach { deactivate(it) }

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

fun safeToEmerge(timer: Timer): Boolean {
    if ( Saucer.active) return false
    if (activeMissiles(SpaceObjects).isNotEmpty()) return false
    for (asteroid in activeAsteroids(SpaceObjects)) {
        if ( asteroid.position.distanceTo(U.CenterOfUniverse) < U.SafeShipDistance ) return false
    }
    return true
}

