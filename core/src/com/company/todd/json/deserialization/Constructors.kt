package com.company.todd.json.deserialization

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.sensor.createRectangleBPWithTGSBGS
import com.company.todd.gui.HealthBar
import com.company.todd.json.serialization.SerializationType
import com.company.todd.objects.creature.Creature
import com.company.todd.objects.weapon.HandWeapon
import com.company.todd.objects.weapon.SimpleMeleeWeapon
import com.company.todd.objects.weapon.Weapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.CloudyPlatform
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidPolygonPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform
import com.company.todd.thinker.StupidMeleeThinker
import com.company.todd.thinker.Thinker
import kotlin.reflect.KClass

object Constructors {
    val constructors: Map<String, JsonType<out InGameObject>>

    init {
        val constructors = mutableMapOf<String, JsonType<out InGameObject>>()

        val bodyPatternType = getBodyPatternType()
        addPassiveObjects(constructors, bodyPatternType)
        addCreatures(constructors, bodyPatternType, getWeapons(), getThinkers())

        this.constructors = constructors
    }

    private fun <T, CT : Any> Map<KClass<out CT>, JsonType<out T>>.toJsonTypeMap() = mapKeys { entry ->
        try {
            (entry.key.annotations.first { it is SerializationType} as SerializationType).type
        } catch (e: NoSuchElementException) {
            throw IllegalArgumentException("Given class has no SerializationType annotation", e)
        }
    }

    private fun getBodyPatternType(): JsonType<out BodyPattern> {
        val map = mapOf(
            "rectangleWithTopGSBottomGS" to JsonType("Rectangle body pattern with top and bottom ground sensors") { _, json ->
                createRectangleBPWithTGSBGS(
                    BodyDef.BodyType.DynamicBody,
                    json["bodyPosition", vector], json["bodySize", vector]
                )
            }
        )

        return JsonType("Body pattern") { game, json -> parseJsonValue(game, json, map) }
    }

    private fun addPassiveObjects(
        map: MutableMap<String, JsonType<out InGameObject>>,
        bodyPatternType: JsonType<out BodyPattern>
    ) {
        // TODO use bodyPatternType
        mapOf(
                SolidRectanglePlatform::class to JsonType("Solid Rectangle Platform") { game, json ->
                    SolidRectanglePlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                SolidPolygonPlatform::class to JsonType("Solid Polygon Platform") { game, json ->
                    SolidPolygonPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json["drawableSize", vector], json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["worldBodyCenter", vector], json["localVertices", vectorArray]
                    )
                },

                HalfCollidedPlatform::class to JsonType("Half Collided Platform") { game, json ->
                    HalfCollidedPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                CloudyPlatform::class to JsonType("Cloudy Platform") { game, json ->
                    CloudyPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["sinceContactTillInactive", float], json["sinceInactiveTillActive", float]
                    )
                },

                Jumper::class to JsonType("Jumper") { game, json ->
                    Jumper(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["pushPower", float]
                    )
                },

                Portal::class to JsonType("Portal") { game, json ->
                    val radius = json["radius", float]
                    Portal(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json["drawableSize", vector, null, Vector2(radius * 2, radius * 2)],
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["center", vector], radius,
                            json["teleportTo", vector], json["teleportDelay", float]
                    )
                },

                Trampoline::class to JsonType("Trampoline") { game, json ->
                    Trampoline(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                Travolator::class to JsonType("Trav0lator") { game, json ->
                    Travolator(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["pushPower", float]
                    )
                }
        ).toJsonTypeMap().let { map.putAll(it) }
    }

    private fun getWeapons(): Map<String, JsonType<out Weapon>> {
        val handWeaponStyle = JsonType("HandWeapon style") { game, json ->
            game!!
            val hand =
                    if (json.has("handDrawableName")) {
                        game.textureManager.loadDrawable(json["handDrawableName", string]) to
                                json["handPosition", vector]
                    } else {
                        null to Vector2()
                    }
            val weapon =
                    if (json.has("weaponDrawableName")) {
                        game.textureManager.loadDrawable(json["weaponDrawableName", string]) to
                                json["weaponPosition", vector]
                    } else {
                        null to Vector2()
                    }

            HandWeapon.Style(hand.first, weapon.first, hand.second, weapon.second, json["origin", vector])
        }

        return mapOf<KClass<out Weapon>, JsonType<out Weapon>>(
                SimpleMeleeWeapon::class to JsonType("Simple Melee Weapon") { game, json ->
                    SimpleMeleeWeapon(
                            json["style", handWeaponStyle, game],
                            json["attackXYWH", rectangle], json["power", float],
                            json["cooldown", float], json["sinceAttackTillDamage", float]
                    )
                }
        ).toJsonTypeMap()
    }

    private fun getThinkers(): Map<String, JsonType<out Thinker>> {
        return mapOf<KClass<out Thinker>, JsonType<out Thinker>>(
            StupidMeleeThinker::class to JsonType("Stupid Melee Thinker") { game, json ->
                StupidMeleeThinker(json["maxDistanceFromTarget", float], json["jumpCooldown", float])
            }
        ).toJsonTypeMap()
    }

    private fun addCreatures(
        map: MutableMap<String, JsonType<out InGameObject>>,
        bodyPatternType: JsonType<out BodyPattern>,
        weapons: Map<String, JsonType<out Weapon>>,
        thinkers: Map<String, JsonType<out Thinker>>
    ) {
        val weaponType = JsonType("Weapon") { game, json -> parseJsonValue(game, json, weapons) }
        val thinkerType = JsonType("Thinker") { game, json -> parseJsonValue(game, json, thinkers) }
        val healthBarType = JsonType("HealthBar") { game, jsonWithPrototype ->
            val json = createJsonValue(jsonWithPrototype)
            HealthBar(
                    json["maxHealth", float], json["stepSize", float], json["animateDuration", float],
                    game!!.textureManager.loadDrawable(json["backgroundDrawableName", string]),
                    game.textureManager.loadDrawable(json["healthDrawableName", string])
            )
        }

        mapOf<KClass<out InGameObject>, JsonType<out InGameObject>>(
            Creature::class to JsonType("Stupid Enemy") { game, json ->
                Creature(
                    game!!,
                    game.textureManager.loadDrawable(json["drawableName", string]),
                    json.get("drawableSize", vector, defaultOther = "bodySize"),
                    json["bodyLowerLeftCornerOffset", vector], json["bodyPattern", bodyPatternType, game],
                    json["weapon", weaponType, game], json["thinker", thinkerType, game],
                    json["healthBar", healthBarType, game], json["speed", float], json["jumpPower", float]
                )
            }
        ).toJsonTypeMap().let { map.putAll(it) }
    }
}