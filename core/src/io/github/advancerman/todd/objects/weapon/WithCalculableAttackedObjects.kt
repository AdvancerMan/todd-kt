package io.github.advancerman.todd.objects.weapon

import io.github.advancerman.todd.objects.base.InGameObject

interface WithCalculableAttackedObjects {
    fun calculateAttackedObjects(): Set<InGameObject>
}
