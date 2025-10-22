# Konsist Architecture Validation

This project uses Konsist to enforce Clean Architecture principles and code quality standards.

## What is Konsist?

Konsist is a Kotlin static code analyzer that helps enforce architectural guidelines and maintain code consistency. It's particularly useful for:

- **Clean Architecture enforcement**: Ensuring proper layer dependencies
- **Code quality**: Enforcing naming conventions and best practices
- **Architectural constraints**: Preventing violations of architectural rules

## Architecture Rules

### Clean Architecture Layer Dependencies

1. **Domain Layer** (`..domain..`)
   - ✅ Should not depend on any other layer
   - ✅ Should not have Android-specific dependencies
   - ✅ Should use Result types for error handling
   - ✅ Should not contain hardcoded strings

2. **Data Layer** (`..data..`)
   - ✅ Should only depend on domain layer
   - ✅ Should contain repository implementations
   - ✅ Should contain data sources

3. **Presentation Layer** (`..presentation..`)
   - ✅ Should only depend on domain layer
   - ✅ Should contain ViewModels and Composables
   - ✅ Should not directly access data sources

### Use Case Rules

- ✅ Use cases should be in domain layer
- ✅ Use cases should implement UseCase interface
- ✅ Use cases should be suspend functions
- ✅ Use cases should have proper documentation

### Repository Rules

- ✅ Repository interfaces should be in domain layer
- ✅ Repository implementations should be in data layer
- ✅ Data sources should be in data layer

## Code Quality Rules

### Naming Conventions
- ✅ Classes: PascalCase (e.g., `UserRepository`)
- ✅ Functions: camelCase (e.g., `getUserById`)
- ✅ Properties: camelCase (e.g., `userName`)

### Documentation
- ✅ All public classes should have KDoc
- ✅ All public functions should have KDoc
- ✅ All public properties should have KDoc

### Code Standards
- ✅ No magic numbers
- ✅ No TODO comments in production code
- ✅ No empty catch blocks
- ✅ No unused imports
- ✅ No hardcoded strings
- ✅ No println statements in production code

### Data Classes
- ✅ Data classes should be immutable (val properties)
- ✅ All sealed classes should be in domain layer
- ✅ All enums should be in domain layer

## Running Konsist

### Available Tasks

```bash
# Run Konsist architecture validation
./gradlew konsist

# Run all verification tools (Detekt + SwiftLint + Konsist)
./gradlew lintAll

# Run specific test classes
./gradlew test --tests "*CleanArchitectureTest*"
./gradlew test --tests "*CodeQualityTest*"
```

### Test Structure

```
shared/src/commonTest/kotlin/org/deafsapps/mobile/kmpflagship/architecture/
├── CleanArchitectureTest.kt    # Clean Architecture rules
└── CodeQualityTest.kt          # Code quality rules
```

## Adding New Rules

To add new Konsist rules:

1. **Create a new test class** in the architecture package
2. **Write test methods** that validate your architectural constraints
3. **Use Konsist API** to query and assert on code structure
4. **Run tests** to validate the rules

### Example: Adding a new rule

```kotlin
@Test
fun `all repositories should be interfaces`() {
    Konsist
        .scopeFromProject()
        .classes()
        .withNameEndingWith("Repository")
        .assert { declaration ->
            declaration.hasInterfaceModifier
        }
}
```

## Configuration

### Dependencies

Konsist is added to the project via:

```kotlin
// gradle/libs.versions.toml
konsist = "0.15.0"

// shared/build.gradle.kts
commonTest.dependencies {
    implementation(libs.konsist)
}
```

### Test Execution

Konsist tests run as part of the standard test suite and are integrated into the verification pipeline.

## Best Practices

1. **Keep rules focused**: Each test should validate one specific architectural constraint
2. **Use descriptive names**: Test names should clearly describe what they validate
3. **Group related rules**: Organize tests by architectural layer or concern
4. **Regular execution**: Run Konsist as part of your CI/CD pipeline
5. **Document violations**: When rules fail, document the architectural decision

## Troubleshooting

### Common Issues

1. **Test failures**: Check if your code violates the architectural constraints
2. **Import issues**: Ensure Konsist is properly added to test dependencies
3. **Package structure**: Verify that your packages follow the expected naming conventions

### Debugging

```bash
# Run with verbose output
./gradlew test --tests "*CleanArchitectureTest*" --info

# Run specific test method
./gradlew test --tests "*CleanArchitectureTest.domain layer should not depend on any other layer"
```

## Integration with CI/CD

Konsist tests are automatically run as part of the verification pipeline:

```yaml
# Example GitHub Actions
- name: Run Architecture Validation
  run: ./gradlew konsist
```

This ensures that architectural violations are caught early in the development process.
