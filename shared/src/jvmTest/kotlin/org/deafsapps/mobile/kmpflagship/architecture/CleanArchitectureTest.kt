package org.deafsapps.mobile.kmpflagship.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.functions
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withPublicModifier
import com.lemonappdev.konsist.api.ext.list.withAnnotation
import com.lemonappdev.konsist.api.ext.list.withName
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.jupiter.api.Test

/**
 * Clean Architecture Konsist rules to enforce architectural constraints.
 * These tests ensure that the project follows Clean Architecture principles.
 */
class CleanArchitectureTest {

    @Test
    fun `'domain' layer should not depend on any other layer`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .withPackage("..domain..")
            .assertTrue(testName = "'domain' layer should not depend on any other layer") { file ->
                file.imports.none { import ->
                    import.name.contains("data") ||
                    import.name.contains("presentation") ||
                    import.name.contains("di") ||
                    import.name.contains("compose")
                }
            }
    }

    @Test
    fun `'data' layer should only depend on domain layer`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .withPackage("..data..")
            .assertTrue(testName = "'data' layer should only depend on 'domain' layer") { file ->
                file.imports.none { import ->
                    import.name.contains("presentation") ||
                    import.name.contains("di") ||
                    import.name.contains("compose")
                }
            }
    }

    @Test
    fun `'presentation' layer should only depend on domain layer`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .withPackage("..presentation..")
            .assertTrue(testName = "'presentation' layer should only depend on 'domain' layer") { file ->
                file.imports.none { import ->
                    import.name.contains("presentation") ||
                            import.name.contains("di") ||
                            import.name.contains("compose")
                }
            }
    }

    @Test
    fun `Use-cases should be in 'domain' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue(testName = "Use-cases should be in 'domain' layer") { declaration ->
                declaration.resideInPackage("..domain..")
            }
    }

    @Test
    fun `Use-cases should implement 'UseCase' interface`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue(testName = "Use-cases should implement 'UseCase' interface") { declaration ->
                declaration.parentInterfaces().any { `interface`  -> `interface`.name == "UseCase" }
            }
    }

    @Test
    fun `Repositories should be interfaces in 'domain' layer`() {
        Konsist
            .scopeFromProject()
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue(testName = "Repositories should be interfaces in 'domain' layer") { declaration ->
                declaration.resideInPackage("..domain..")
            }
    }

    @Test
    fun `Repository implementations should be in 'data' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue(testName = "Repository implementations should be in 'data' layer") { declaration ->
                declaration.resideInPackage("..data..")
            }
    }

    @Test
    fun `Data-sources should be in 'data' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("DataSource")
            .assertTrue(testName = "Data-sources should be in 'data' layer") { declaration ->
                declaration.resideInPackage("..data..")
            }
    }

    @Test
    fun `View-models should be in 'presentation' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("ViewModel")
            .assertTrue(testName = "View-models should be in 'presentation' layer") { declaration ->
                declaration.resideInPackage("..presentation..")
            }
    }

    @Test
    fun `Composables should be in 'presentation' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withAnnotation { annotation -> annotation.name == "Composable" }
            .assertTrue(testName = "Composables should be in 'presentation' layer") { declaration ->
                declaration.resideInPackage("..presentation..")
            }
    }

    @Test
    fun `'Result' type should be used for error handling`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .withPackage("..domain..")
            .assertTrue(testName = "'Result' type should be used for error handling") { declaration ->
                // Check that domain classes use Result type for error handling
                val hasResultUsage = declaration.imports.any { import ->
                    import.name.contains("Result")
                } || declaration.text.contains("Result<")
                // Allow exceptions only for specific cases
                val hasAllowedExceptions = declaration.text.contains("IllegalArgumentException") ||
                    declaration.text.contains("IllegalStateException")

                hasResultUsage || hasAllowedExceptions
            }
    }

    @Test
    fun `No hardcoded strings in 'domain' layer`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("..domain..")
            .assertTrue(testName = "No hardcoded strings in 'domain' layer") { declaration ->
                val hasHardcodedStrings = declaration.text.contains("\"[^\"]*\"") &&
                    !declaration.text.contains("// TODO") &&
                    !declaration.text.contains("// FIXME")

                !hasHardcodedStrings
            }
    }

    @Test
    fun `'domain' layer should not have Android specific code`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("..domain..")
            .assertTrue(testName = "'domain' layer should not have Android specific code") { declaration ->
                val hasAndroidCode = declaration.text.contains("@AndroidEntryPoint") ||
                    declaration.text.contains("@HiltViewModel") ||
                    declaration.text.contains("androidx") ||
                    declaration.text.contains("compose")

                !hasAndroidCode
            }
    }

    @Test
    fun `'data' layer entities should be data classes`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPackage("..data..")
            .withNameEndingWith("Entity")
            .assertTrue(testName = "'data' layer entities should be data classes") { declaration ->
                declaration.hasDataModifier
            }
    }

    @Test
    fun `'Presentation' layer should not directly access data sources`() {
        Konsist
            .scopeFromSourceSet("commonMain", "androidMain", "iosMain")
            .files
            .withPackage("..presentation..")
            .assertTrue(testName = "'Presentation' layer should not directly access data sources") { file ->
                file.imports.any { import ->
                    import.name.contains("datasource") ||
                    import.name.contains("api") ||
                    import.name.contains("database")
                }
            }
    }

    @Test
    fun `All public classes should have documentation`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withPublicModifier()
            .assertTrue(testName = "All public classes should have documentation") { declaration ->
                declaration.hasKDoc
            }
    }

    @Test
    fun `Use-cases should be suspend functions`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            // Get all functions named 'invoke' within these classes
            .functions()
            .withName("invoke")
            // Assert that every single one of them has the suspend modifier
            .assertTrue(testName = "The 'invoke' method in a UseCase must be a suspend function") { function ->
                function.hasSuspendModifier
            }
    }
}
