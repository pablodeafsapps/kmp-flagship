//package org.deafsapps.mobile.kmpflagship.architecture
//
//import com.lemonappdev.konsist.Konsist
//import com.lemonappdev.konsist.api.KonsistInfo
//import kotlin.test.Test
//
///**
// * Clean Architecture Konsist rules to enforce architectural constraints.
// * These tests ensure that the project follows Clean Architecture principles.
// */
//class CleanArchitectureTest {
//
//    @Test
//    fun `domain layer should not depend on any other layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .assert { declaration ->
//                val hasInvalidDependencies = declaration.imports.any { import ->
//                    import.name.contains("data") ||
//                    import.name.contains("presentation") ||
//                    import.name.contains("view") ||
//                    import.name.contains("android") ||
//                    import.name.contains("compose") ||
//                    import.name.contains("ui")
//                }
//
//                !hasInvalidDependencies
//            }
//    }
//
//    @Test
//    fun `data layer should only depend on domain layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..data..")
//            .assert { declaration ->
//                val hasInvalidDependencies = declaration.imports.any { import ->
//                    import.name.contains("presentation") ||
//                    import.name.contains("view") ||
//                    import.name.contains("compose") ||
//                    import.name.contains("ui")
//                }
//
//                !hasInvalidDependencies
//            }
//    }
//
//    @Test
//    fun `presentation layer should only depend on domain layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..presentation..")
//            .assert { declaration ->
//                val hasInvalidDependencies = declaration.imports.any { import ->
//                    import.name.contains("data") ||
//                    import.name.contains("repository") ||
//                    import.name.contains("datasource")
//                }
//
//                !hasInvalidDependencies
//            }
//    }
//
//    @Test
//    fun `use cases should be in domain layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("UseCase")
//            .assert { declaration ->
//                declaration.resideInPackage("..domain..")
//            }
//    }
//
//    @Test
//    fun `use cases should implement UseCase interface`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("UseCase")
//            .assert { declaration ->
//                declaration.implements("UseCase")
//            }
//    }
//
//    @Test
//    fun `repositories should be interfaces in domain layer`() {
//        Konsist
//            .scopeFromProject()
//            .interfaces()
//            .withNameEndingWith("Repository")
//            .assert { declaration ->
//                declaration.resideInPackage("..domain..")
//            }
//    }
//
//    @Test
//    fun `repository implementations should be in data layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("RepositoryImpl")
//            .assert { declaration ->
//                declaration.resideInPackage("..data..")
//            }
//    }
//
//    @Test
//    fun `data sources should be in data layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("DataSource")
//            .assert { declaration ->
//                declaration.resideInPackage("..data..")
//            }
//    }
//
//    @Test
//    fun `view models should be in presentation layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("ViewModel")
//            .assert { declaration ->
//                declaration.resideInPackage("..presentation..")
//            }
//    }
//
//    @Test
//    fun `composables should be in presentation layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withAnnotation("Composable")
//            .assert { declaration ->
//                declaration.resideInPackage("..presentation..")
//            }
//    }
//
//    @Test
//    fun `domain entities should be data classes`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .withNameEndingWith("Entity")
//            .assert { declaration ->
//                declaration.hasDataModifier
//            }
//    }
//
//    @Test
//    fun `domain entities should not have Android dependencies`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .withNameEndingWith("Entity")
//            .assert { declaration ->
//                val hasAndroidDependencies = declaration.imports.any { import ->
//                    import.name.contains("android") ||
//                    import.name.contains("compose") ||
//                    import.name.contains("ui")
//                }
//
//                !hasAndroidDependencies
//            }
//    }
//
//    @Test
//    fun `result type should be used for error handling`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .assert { declaration ->
//                // Check that domain classes use Result type for error handling
//                val hasResultUsage = declaration.imports.any { import ->
//                    import.name.contains("Result")
//                } || declaration.text.contains("Result<")
//
//                // Allow exceptions only for specific cases
//                val hasAllowedExceptions = declaration.text.contains("IllegalArgumentException") ||
//                    declaration.text.contains("IllegalStateException")
//
//                hasResultUsage || hasAllowedExceptions
//            }
//    }
//
//    @Test
//    fun `no hardcoded strings in domain layer`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .assert { declaration ->
//                val hasHardcodedStrings = declaration.text.contains("\"[^\"]*\"") &&
//                    !declaration.text.contains("// TODO") &&
//                    !declaration.text.contains("// FIXME")
//
//                !hasHardcodedStrings
//            }
//    }
//
//    @Test
//    fun `domain layer should not have Android specific code`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..domain..")
//            .assert { declaration ->
//                val hasAndroidCode = declaration.text.contains("@AndroidEntryPoint") ||
//                    declaration.text.contains("@HiltViewModel") ||
//                    declaration.text.contains("androidx") ||
//                    declaration.text.contains("compose")
//
//                !hasAndroidCode
//            }
//    }
//
//    @Test
//    fun `presentation layer should not directly access data sources`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPackage("..presentation..")
//            .assert { declaration ->
//                val hasDirectDataSourceAccess = declaration.imports.any { import ->
//                    import.name.contains("datasource") ||
//                    import.name.contains("api") ||
//                    import.name.contains("database")
//                }
//
//                !hasDirectDataSourceAccess
//            }
//    }
//
//    @Test
//    fun `all public classes should have documentation`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withPublicModifier
//            .assert { declaration ->
//                declaration.hasKDoc
//            }
//    }
//
//    @Test
//    fun `use cases should be suspend functions`() {
//        Konsist
//            .scopeFromProject()
//            .classes()
//            .withNameEndingWith("UseCase")
//            .assert { declaration ->
//                declaration.hasFunctionWithName("invoke") &&
//                declaration.getFunctionByName("invoke")?.hasSuspendModifier == true
//            }
//    }
//}
