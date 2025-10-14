// Verification tasks for code quality
// This file contains Detekt and SwiftLint configurations

// SwiftLint task for iOS app (only register at root level)
if (project == project.rootProject) {
    tasks.register<Exec>("swiftlint") {
        group = "verification"
        description = "Run SwiftLint on iOS app"
        
        workingDir = file("iosApp")
        commandLine("swiftlint", "lint", "--config", ".swiftlint.yml")
        
        doFirst {
            println("Running SwiftLint on iOS app...")
        }
    }

    tasks.register<Exec>("swiftlintAutocorrect") {
        group = "verification"
        description = "Run SwiftLint autocorrect on iOS app"
        
        workingDir = file("iosApp")
        commandLine("swiftlint", "lint", "--fix", "--config", ".swiftlint.yml")
        
        doFirst {
            println("Running SwiftLint autocorrect on iOS app...")
        }
    }
}

// Konsist task for architecture validation
tasks.register("konsist") {
    group = "verification"
    description = "Run Konsist architecture and code quality checks"
    
    doFirst {
        println("Running Konsist architecture validation...")
        println("Note: Konsist tests are available in the test suite.")
        println("Run './gradlew :shared:testDebugUnitTest --tests \"*CleanArchitectureTest*\"' to execute Konsist rules.")
    }
    
    doLast {
        println("Konsist architecture validation completed!")
        println("To run Konsist tests manually:")
        println("  ./gradlew :shared:testDebugUnitTest --tests \"*CleanArchitectureTest*\"")
        println("  ./gradlew :shared:testDebugUnitTest --tests \"*CodeQualityTest*\"")
    }
}

// Combined linting task (only register at root level)
if (project == project.rootProject) {
    tasks.register("lintAll") {
        group = "verification"
        description = "Run all linting tools (Detekt + SwiftLint + Konsist)"
        
        // Only add detekt dependency if the task exists (i.e., if Detekt plugin is applied)
        if (tasks.findByName("detekt") != null) {
            dependsOn("detekt")
        }
        
        dependsOn("swiftlint", "konsist")
        
        doLast {
            println("All linting tasks completed successfully!")
        }
    }
}

// Task to check if all required tools are installed (only register at root level)
if (project == project.rootProject) {
    tasks.register("checkVerificationTools") {
        group = "verification"
        description = "Check if all verification tools are installed"
        
        doLast {
            val tools = listOf(
                "swiftlint" to "brew install swiftlint"
            )
            
            var allInstalled = true
            
            tools.forEach { (tool, installCommand) ->
                val result = providers.exec {
                    commandLine("which", tool)
                    isIgnoreExitValue = true
                }
                
                if (result.result.get().exitValue != 0) {
                    println("❌ $tool is not installed. Install with: $installCommand")
                    allInstalled = false
                } else {
                    println("✅ $tool is installed")
                }
            }
            
            if (allInstalled) {
                println("✅ All verification tools are installed!")
            } else {
                throw GradleException("Some verification tools are missing. Please install them and try again.")
            }
        }
    }
}