package com.ronjeffries.flat

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

fun newAsteroid(): SpaceObject = SpaceObject(SpaceObjectType.ASTEROID, 0.0, 0.0, 0.0, 0.0, 0.0, false)
    .also { it.scale = 4.0 }

fun newMissile(): SpaceObject {
    return SpaceObject(SpaceObjectType.MISSILE, 0.0, 0.0, 0.0, 0.0, 0.0, false)
        .also { spaceObject ->
            val missileTimer = Timer(spaceObject, U.MissileTime, true) { timer -> deactivate(timer.entity) }
            addComponent(spaceObject, missileTimer)
        }
}

fun newSaucer(): SpaceObject = SpaceObject(SpaceObjectType.SAUCER, 0.0, 0.0, 0.0, 0.0, 0.0, false).also { addComponent(it, SaucerTimer(it)) }

fun newShip(): SpaceObject = SpaceObject(SpaceObjectType.SHIP, 0.0, 0.0, 0.0, 0.0, 0.0, false)
    .also { spaceObject ->
        val shipTimer = Timer(
            spaceObject,
            U.ShipDelay,
            false,
            { timer: Timer -> safeToEmerge(timer) }
        ) { activateShip() }
        addComponent(spaceObject, shipTimer)
    }