package com.company.todd.json

import com.badlogic.gdx.math.Vector2
import com.company.todd.gui.HealthBar
import com.company.todd.objects.active.creature.enemy.StupidEnemy
import com.company.todd.objects.active.creature.weapon.HandWeapon
import com.company.todd.objects.active.creature.weapon.SimpleMeleeWeapon
import com.company.todd.objects.active.creature.weapon.Weapon
import com.company.todd.objects.base.InGameObject
import com.company.todd.objects.passive.interactive.Jumper
import com.company.todd.objects.passive.interactive.Portal
import com.company.todd.objects.passive.interactive.Trampoline
import com.company.todd.objects.passive.interactive.Travolator
import com.company.todd.objects.passive.platform.CloudyPlatform
import com.company.todd.objects.passive.platform.HalfCollidedPlatform
import com.company.todd.objects.passive.platform.SolidPolygonPlatform
import com.company.todd.objects.passive.platform.SolidRectanglePlatform
import com.company.todd.util.putAll

object Constructors {
    val constructors: Map<String, JsonType<out InGameObject>>

    init {
        val constructors = mutableMapOf<String, JsonType<out InGameObject>>()

        addPassiveObjects(constructors)
        addCreatures(constructors, getWeapons())

        this.constructors = constructors
    }

    private fun addPassiveObjects(map: MutableMap<String, JsonType<out InGameObject>>) {
        map.putAll(
                "solidRectangle" to JsonType("Solid Rectangle Platform") { game, json ->
                    SolidRectanglePlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                "solidPolygon" to JsonType("Solid Polygon Platform") { game, json ->
                    SolidPolygonPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json["drawableSize", vector], json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["worldBodyCenter", vector], json["localVertices", vectorArray]
                    )
                },

                "halfCollided" to JsonType("Half Collided Platform") { game, json ->
                    HalfCollidedPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                "cloudy" to JsonType("Cloudy Platform") { game, json ->
                    CloudyPlatform(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["sinceContactTillInactive", float], json["sinceInactiveTillActive", float]
                    )
                },

                "jumper" to JsonType("Jumper") { game, json ->
                    Jumper(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["pushPower", float]
                    )
                },

                "portal" to JsonType("Portal") { game, json ->
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

                "trampoline" to JsonType("Trampoline") { game, json ->
                    Trampoline(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector]
                    )
                },

                "travolator" to JsonType("Trav0lator") { game, json ->
                    Travolator(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector, game, Vector2()],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["pushPower", float]
                    )
                }
        )
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

        return mapOf(
                "simpleMeleeWeapon" to JsonType("Simple Melee Weapon") { game, json ->
                    SimpleMeleeWeapon(
                            json["style", handWeaponStyle, game],
                            json["attackXYWH", rectangle], json["power", float],
                            json["cooldown", float], json["sinceAttackTillDamage", float]
                    )
                }
        )
    }

    private fun addCreatures(map: MutableMap<String, JsonType<out InGameObject>>,
                             weapons: Map<String, JsonType<out Weapon>>) {
        val weapon = JsonType("Weapon") { game, json -> parseJsonValue(game, json, weapons) }
        val healthBar = JsonType("HealthBar") { game, jsonWithPrototype ->
            val json = createJsonValue(jsonWithPrototype)
            HealthBar(
                    json["maxHealth", float], json["stepSize", float], json["animateDuration", float],
                    game!!.textureManager.loadDrawable(json["backgroundDrawableName", string]),
                    game.textureManager.loadDrawable(json["healthDrawableName", string])
            )
        }

        map.putAll(
                "stupidEnemy" to JsonType("Stupid Enemy") { game, json ->
                    StupidEnemy(
                            game!!,
                            game.textureManager.loadDrawable(json["drawableName", string]),
                            json.get("drawableSize", vector, defaultOther = "bodySize"),
                            json["bodyLowerLeftCornerOffset", vector],
                            json["bodyPosition", vector], json["bodySize", vector],
                            json["weapon", weapon, game], json["healthBar", healthBar, game],
                            json["speed", float], json["jumpPower", float],
                            json["jumpCooldown", float], json["maxDistanceFromTarget", float]
                    )
                }
        )
    }
}
