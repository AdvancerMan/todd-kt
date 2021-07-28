package com.company.todd.json.docs

import org.jetbrains.dokka.CoreExtensions
import org.jetbrains.dokka.plugability.DokkaPlugin

class JsonDocsDokkaPlugin : DokkaPlugin() {
    val serializationTypeTransformer by extending {
        CoreExtensions.documentableTransformer providing ::SerializationTypeTransformer
    }
}
