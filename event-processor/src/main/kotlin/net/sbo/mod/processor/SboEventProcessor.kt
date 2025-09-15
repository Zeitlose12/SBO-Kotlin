package net.sbo.mod.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

class SboEventProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("net.sbo.mod.utils.events.annotations.SboEvent")
        val generatedObjects = mutableListOf<String>()

        // Group @SboEvent functions by class
        val classSymbols = symbols.filterIsInstance<KSFunctionDeclaration>()
            .groupBy { it.parentDeclaration as? KSClassDeclaration }

        classSymbols.forEach { (clazz, functions) ->
            if (clazz == null) return@forEach
            val packageName = clazz.packageName.asString()
            val className = clazz.simpleName.asString()
            val fileName = "${className}_SboEventRegister"

            generatedObjects.add("$packageName.$fileName")

            val isObject = clazz.classKind == ClassKind.OBJECT
            val instanceRef = if (isObject) className else "$className()"

            val file = codeGenerator.createNewFile(
                Dependencies(false, clazz.containingFile!!),
                packageName,
                fileName
            )

            OutputStreamWriter(file).use { writer ->
                val functionCalls = functions.joinToString("\n") { fn ->
                    val paramType = fn.parameters.firstOrNull()?.type?.resolve()?.declaration?.qualifiedName?.asString()
                    if (paramType == null) {
                        "// Cannot resolve type for ${fn.simpleName.asString()}"
                    } else {
                        "EventBus.on($paramType::class) { e -> $instanceRef.${fn.simpleName.asString()}(e) }"
                    }
                }

                writer.write("""
                    package $packageName

                    import net.sbo.mod.utils.events.EventBus

                    object $fileName {
                        fun register() {
                            $functionCalls
                        }
                    }
                """.trimIndent())
            }
        }

        if (generatedObjects.isNotEmpty()) {
            val registryFile = codeGenerator.createNewFile(
                Dependencies(false),
                "net.sbo.mod.utils.events",
                "SboEventGeneratedRegistry"
            )
            OutputStreamWriter(registryFile).use { writer ->
                writer.write("""
                    package net.sbo.mod.utils.events

                    object SboEventGeneratedRegistry {
                        fun registerAll() {
                            ${generatedObjects.joinToString("\n") { "$it.register()" }}
                        }
                    }
                """.trimIndent())
            }
        }

        return emptyList()
    }
}
