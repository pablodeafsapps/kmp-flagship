package org.deafsapps.mobile.kmpflagship.architecture

import com.lemonappdev.konsist.Konsist
import kotlin.test.Test

/**
 * Code quality Konsist rules to enforce coding standards and best practices.
 */
class CodeQualityTest {

    @Test
    fun `all classes should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                val name = declaration.name
                name.matches(Regex("[A-Z][a-zA-Z0-9]*")) && 
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `all functions should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .functions()
            .assert { declaration ->
                val name = declaration.name
                name.matches(Regex("[a-z][a-zA-Z0-9]*")) && 
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `all properties should follow naming conventions`() {
        Konsist
            .scopeFromProject()
            .properties()
            .assert { declaration ->
                val name = declaration.name
                name.matches(Regex("[a-z][a-zA-Z0-9]*")) && 
                !name.contains("_") &&
                !name.contains("-")
            }
    }

    @Test
    fun `no magic numbers should be used`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                val hasMagicNumbers = declaration.text.contains(Regex("\\b[0-9]+\\b")) &&
                    !declaration.text.contains("version") &&
                    !declaration.text.contains("id") &&
                    !declaration.text.contains("index")
                
                !hasMagicNumbers
            }
    }

    @Test
    fun `no TODO comments should remain in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                !declaration.text.contains("TODO") &&
                !declaration.text.contains("FIXME") &&
                !declaration.text.contains("HACK")
            }
    }

    @Test
    fun `all public functions should have documentation`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withPublicModifier
            .assert { declaration ->
                declaration.hasKDoc
            }
    }

    @Test
    fun `all public properties should have documentation`() {
        Konsist
            .scopeFromProject()
            .properties()
            .withPublicModifier
            .assert { declaration ->
                declaration.hasKDoc
            }
    }

    @Test
    fun `no empty catch blocks`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                !declaration.text.contains("catch") ||
                !declaration.text.contains("catch (") ||
                declaration.text.contains("// TODO") ||
                declaration.text.contains("// FIXME")
            }
    }

    @Test
    fun `no unused imports`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                val imports = declaration.imports
                val usedImports = imports.filter { import ->
                    declaration.text.contains(import.name.split(".").last())
                }
                
                imports.size == usedImports.size
            }
    }

    @Test
    fun `all data classes should be immutable`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withDataModifier
            .assert { declaration ->
                val properties = declaration.properties
                properties.all { property ->
                    property.hasValModifier
                }
            }
    }

    @Test
    fun `all sealed classes should be in domain layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withSealedModifier
            .assert { declaration ->
                declaration.resideInPackage("..domain..")
            }
    }

    @Test
    fun `all enums should be in domain layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withEnumModifier
            .assert { declaration ->
                declaration.resideInPackage("..domain..")
            }
    }

    @Test
    fun `no hardcoded strings in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                val hasHardcodedStrings = declaration.text.contains("\"[^\"]*\"") &&
                    !declaration.text.contains("// TODO") &&
                    !declaration.text.contains("// FIXME") &&
                    !declaration.text.contains("// Test")
                
                !hasHardcodedStrings
            }
    }

    @Test
    fun `all test classes should be in test packages`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("Test")
            .assert { declaration ->
                declaration.resideInPackage("..test..") ||
                declaration.resideInPackage("..commonTest..")
            }
    }

    @Test
    fun `all test functions should be in test classes`() {
        Konsist
            .scopeFromProject()
            .functions()
            .withNameStartingWith("test")
            .assert { declaration ->
                declaration.parentClass?.resideInPackage("..test..") == true ||
                declaration.parentClass?.resideInPackage("..commonTest..") == true
            }
    }

    @Test
    fun `no println statements in production code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .assert { declaration ->
                !declaration.text.contains("println") &&
                !declaration.text.contains("print(")
            }
    }

    @Test
    fun `all companion objects should be private`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withCompanionModifier
            .assert { declaration ->
                declaration.hasPrivateModifier
            }
    }

    @Test
    fun `all constants should be in companion objects`() {
        Konsist
            .scopeFromProject()
            .properties()
            .withNameInUpperCase()
            .assert { declaration ->
                declaration.parentClass?.hasCompanionModifier == true ||
                declaration.hasConstModifier
            }
    }
}
