package com.company.todd.json.deserialization

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.company.todd.box2d.bodyPattern.base.BodyPattern
import com.company.todd.box2d.bodyPattern.createCircleBP
import com.company.todd.box2d.bodyPattern.createPolygonBPWithTGS
import com.company.todd.box2d.bodyPattern.createRectangleBPWithTGS
import com.company.todd.box2d.bodyPattern.createRectangleBPWithTGSBGS
import com.company.todd.gui.HealthBar
import com.company.todd.json.SerializationType
import com.company.todd.objects.creature.Creature
import com.company.todd.objects.weapon.HandWeapon
import com.company.todd.objects.weapon.SimpleMeleeWeapon
import com.company.todd.objects.weapon.Weapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.*
import com.company.todd.thinker.StupidMeleeThinker
import com.company.todd.thinker.Thinker
import com.company.todd.thinker.operated.ScheduledThinker
import kotlin.reflect.KClass

object Constructors {
    val constructors: Map<String, JsonType<out InGameObject>>

    val b2dTypes = mapOf(
        "dynamic" to BodyDef.BodyType.DynamicBody,
        "kinematic" to BodyDef.BodyType.KinematicBody,
        "static" to BodyDef.BodyType.StaticBody
    )

    init {
        val constructors = mutableMapOf<String, JsonType<out InGameObject>>()

        val bodyPatternType = getBodyPatternType()
        addPassiveObjects(constructors, bodyPatternType)
        addCreatures(constructors, bodyPatternType, getWeapons(), getThinkers())

        this.constructors = constructors
    }

    private fun <T, CT : Any> Map<KClass<out CT>, JsonType<out T>>.toJsonTypeMap() = mapKeys { entry ->
        try {
            (entry.key.annotations.first { it is SerializationType } as SerializationType).type
        } catch (e: NoSuchElementException) {
            throw IllegalArgumentException("Given class has no SerializationType annotation", e)
        }
    }

    private fun getBodyPatternType(): JsonType<out BodyPattern> {
        val b2dType = JsonType("Box2D body type") { _, json ->
            val jsonValue = json.asString()
            b2dTypes[jsonValue] ?: throw IllegalArgumentException(
                "Unexpected b2d type $jsonValue (allowed: ${b2dTypes.keys})"
            )
        }

        val map = mapOf(
            "rectangleWithTopGSBottomGS" to JsonType("Rectangle body pattern with top and bottom ground sensors") { _, json ->
                createRectangleBPWithTGSBGS(
                    json["b2dType", b2dType],
                    json["bodyPosition", vector],
                    json["bodySize", vector]
                )
            },
            "rectangleWithTopGS" to JsonType("Rectangle body pattern with top ground sensor") { _, json ->
                createRectangleBPWithTGS(
                    json["b2dType", b2dType],
                    json["bodyPosition", vector],
                    json["bodySize", vector]
                )
            },
            "polygonWithTopGS" to JsonType("Polygon body pattern with top ground sensor") { _, json ->
                createPolygonBPWithTGS(
                    json["b2dType", b2dType],
                    json["worldBodyCenter", vector],
                    json["localVertices", vectorArray]
                )
            },
            "circle" to JsonType("Polygon body pattern with top ground sensor") { _, json ->
                createCircleBP(json["b2dType", b2dType], json["bodyCenter", vector], json["bodyRadius", float])
            },
        )

        val innerJsonType = JsonType("Body pattern") { game, json ->
            parseJsonValue(game, json, map, "bodyPatternType")
        }

        return JsonType("Body pattern") { game, json ->
            if (json["bodyPattern"] != null) {
                json["bodyPattern", innerJsonType, game]
            } else {
                innerJsonType.constructor(game, json)
            }
        }
    }

    private fun addPassiveObjects(
        map: MutableMap<String, JsonType<out InGameObject>>,
        bodyPatternType: JsonType<out BodyPattern>
    ) {
        // FIXME: resource leak on exception (leaking textures)
        mapOf(
                SolidPlatform::class to JsonType("Solid Platform") { game, json ->
                    SolidPlatform(
                        game!!,
                        game.textureManager.loadDrawable(json["drawableName", string]),
                        json.get("drawableSize", vector, defaultOther = "bodySize"),
                        json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                        bodyPatternType.constructor(game, json)
                    )
                },

                HalfCollidedPlatform::class to JsonType("Half Collided Platform") { game, json ->
                    HalfCollidedPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json)
                    )
                },

                CloudyPlatform::class to JsonType("Cloudy Platform") { game, json ->
                    CloudyPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json), json["sinceContactTillInactive", float],
                            json["sinceInactiveTillActive", float]
                    )
                },

                Jumper::class to JsonType("Jumper") { game, json ->
                    Jumper(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json), json["pushPower", float]
                    )
                },

                Portal::class to JsonType("Portal") { game, json ->
                    val radius = json["bodyRadius"]?.asFloat()
                    Portal(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json["drawableSize", vector, null, radius?.let { Vector2(it * 2, it * 2) }],
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json), json["teleportTo", vector],
                            json["teleportDelay", float]
                    )
                },

                Trampoline::class to JsonType("Trampoline") { game, json ->
                    Trampoline(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json)
                    )
                },

                Travolator::class to JsonType("Trav0lator") { game, json ->
                    Travolator(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            bodyPatternType.constructor(game, json), json["pushPower", float]
                    )
                }
        ).toJsonTypeMap().let { map.putAll(it) }
    }

    private fun getWeapons(): Map<String, JsonType<out Weapon>> {
        val handWeaponStyle = JsonType("HandWeapon style") { game, json ->
            game!!
            val hand =
                    if (json.has("handDrawableName") && !json["handDrawableName"].isNull) {
                        game.textureManager.loadDrawable(json["handDrawableName", string]) to
                                json["handPosition", vector]
                    } else {
                        null to Vector2()
                    }
            val weapon =
                    if (json.has("weaponDrawableName") && !json["weaponDrawableName"].isNull) {
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
        val weaponType = JsonType("Weapon") { game, json ->
            if (json.isNull) null else parseJsonValue(game, json, weapons)
        }
        val thinkerType = JsonType("Thinker") { game, json -> parseJsonValue(game, json, thinkers) }
        val healthBarType = JsonType("HealthBar") { game, jsonWithPrototype ->
            val json = createJsonValue(jsonWithPrototype)
            HealthBar(
                    json["maxHealth", float],
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
                    json["bodyLowerLeftCornerOffset", vector], bodyPatternType.constructor(game, json),
                    if (json["weapon"].isNull) null else json["weapon", weaponType, game],
                    json["thinker", thinkerType, game, ScheduledThinker()],
                    json["healthBar", healthBarType, game], json["speed", float], json["jumpPower", float]
                )
            }
        ).toJsonTypeMap().let { map.putAll(it) }
    }
}
