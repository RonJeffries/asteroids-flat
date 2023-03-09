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