package org.deafsapps.mobile.kmpflagship.quality

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withDataModifier
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withModifier
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withPublicModifier
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withoutModifier
import com.lemonappdev.konsist.api.ext.list.withAnnotations
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withoutAnnotations
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Code quality Konsist rules to enforce coding standards and best practices.
 */
class CodeQualityTest {

    @Test
    fun `All classes should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assertTrue(testName = "All classes should follow naming conventions") { declaration ->
                val name = declaration.name
                name.matches(Regex("[A-Z][a-zA-Z0-9]*")) &&
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `All functions should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withoutAnnotations()
            .assertTrue(testName = "All functions should follow naming conventions") { declaration ->
                val name = declaration.name
                name.matches(Regex("[a-z][a-zA-Z0-9]*")) &&
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `All properties should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .properties()
            .withoutModifier(KoModifier.CONST)
            .assertTrue(testName = "All properties should follow naming conventions") { declaration ->
                val name = declaration.name
                name.matches(Regex("[a-z][a-zA-Z0-9]*")) &&
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `No magic numbers should be used`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assertTrue(testName = "No magic numbers should be used") { declaration ->
                val hasMagicNumbers = declaration.text.contains(Regex("\\b[0-9]+\\b")) &&
                    !declaration.text.contains("version") &&
                    !declaration.text.contains("id") &&
                    !declaration.text.contains("index")

                !hasMagicNumbers
            }
    }

    @Test
    fun `No TODO comments should remain in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNoAnnotatedFunctions()
            .assertTrue(testName = "No TODO comments should remain in production code") { declaration ->
                !declaration.text.contains("TODO") &&
                !declaration.text.contains("FIXME") &&
                !declaration.text.contains("HACK")
            }
    }

    @Test
    fun `All public functions should have documentation`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withPublicModifier()
            .assertTrue(testName = "All public functions should have documentation") { declaration ->
                declaration.hasKDoc
            }
    }

    @Test
    fun `All public properties should have documentation`() {
        Konsist
            .scopeFromProject()
            .properties()
            .withPublicModifier()
            .assertTrue(testName = "All public properties should have documentation") { declaration ->
                declaration.hasKDoc
            }
    }

    @Test
    fun `No empty catch blocks`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assertTrue(testName = "No empty catch blocks") { declaration ->
                !declaration.text.contains("catch") ||
                !declaration.text.contains("catch (") ||
                declaration.text.contains("// TODO") ||
                declaration.text.contains("// FIXME")
            }
    }

    @Test
    fun `No unused imports`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .assertTrue(testName = "No unused imports") { file ->
                val imports = file.imports
                val usedImports = imports.filter { import ->
                    file.text.contains(import.name.split(".").last())
                }

                imports.size == usedImports.size
            }
    }

    @Test
    fun `All data classes should be immutable`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withDataModifier()
            .assertTrue(testName = "All data classes should be immutable") { declaration ->
                val properties = declaration.properties()
                properties.all { property ->
                    property.isVal
                }
            }
    }

    @Test
    fun `All sealed classes should be in 'domain' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withModifier(KoModifier.SEALED)
            .assertTrue(testName = "All sealed classes should be in 'domain' layer") { declaration ->
                declaration.resideInPackage("..domain..")
            }
    }

    @Test
    fun `No hardcoded strings in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assertTrue(testName = "No hardcoded strings in production code") { declaration ->
                val hasHardcodedStrings = declaration.text.contains("\"[^\"]*\"") &&
                    !declaration.text.contains("// TODO") &&
                    !declaration.text.contains("// FIXME") &&
                    !declaration.text.contains("// Test")

                !hasHardcodedStrings
            }
    }

    @Test
    fun `All test classes should be in test packages`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Test")
            .assertTrue(testName = "All test classes should be in test packages") { declaration ->
                declaration.resideInSourceSet("androidTest") ||
                declaration.resideInSourceSet("iosTest") ||
                declaration.resideInSourceSet("commonTest") ||
                declaration.resideInSourceSet("jvmTest")
            }
    }

    @Test
    fun `No 'println' statements in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNoAnnotatedFunctions()
            .assertTrue(testName = "No 'println' statements in production code") { declaration ->
                !declaration.text.contains("println") &&
                !declaration.text.contains("print(")
            }
    }

    @Test
    fun `All companion objects should be private`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withModifier(KoModifier.COMPANION)
            .assertTrue(testName = "All companion objects should be private") { declaration ->
                declaration.hasPrivateModifier
            }
    }
}

private fun List<KoClassDeclaration>.withNoAnnotatedFunctions(): List<KoClassDeclaration> =
    filter { c -> c.functions().withAnnotations().isEmpty() }
