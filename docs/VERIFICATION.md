# Code Verification Setup

This project uses static code analysis tools to maintain code quality across both Kotlin and Swift codebases.

## Tools Used

### Kotlin Code Analysis
- **Detekt**: Static code analyzer for Kotlin
- **Configuration**: `detekt.yml` (root level)
- **Modules**: `shared`, `androidApp`

### Swift Code Analysis
- **SwiftLint**: Static code analyzer for Swift
- **Configuration**: `iosApp/.swiftlint.yml`
- **Module**: `iosApp`

## Available Tasks

### All Projects
```bash
# Run all linting tools (Detekt + SwiftLint)
./gradlew lintAll

# Check if all verification tools are installed
./gradlew checkVerificationTools
```

### Kotlin Projects
```bash
# Run Detekt on all Kotlin modules
./gradlew detekt

# Run Detekt on specific module
./gradlew :shared:detekt
./gradlew :androidApp:detekt
```

### Swift Projects
```bash
# Run SwiftLint on iOS app
./gradlew swiftlint

# Run SwiftLint with auto-fix
./gradlew swiftlintAutocorrect
```

## Installation

### Detekt
Detekt is automatically configured via Gradle plugins - no manual installation required.

### SwiftLint
```bash
# Install SwiftLint using the provided script
./scripts/install-swiftlint.sh

# Or install manually
brew install swiftlint
```

## Configuration

### Detekt Configuration
- **File**: `detekt.yml` (root level)
- **Rules**: Comprehensive Kotlin code quality rules
- **Thresholds**: Customized for the project needs

### SwiftLint Configuration
- **File**: `iosApp/.swiftlint.yml`
- **Rules**: Swift-specific code quality rules
- **Line Length**: 120 chars warning, 200 chars error
- **Complexity**: Function complexity limits

## Integration

The verification tasks are available in all modules through the `verification.gradle.kts` script:

- **Root**: `apply(from = "verification.gradle.kts")`
- **Modules**: `apply(from = "../verification.gradle.kts")`

## CI/CD Integration

These tasks can be easily integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions
- name: Run Code Quality Checks
  run: ./gradlew lintAll
```

## Customization

### Adding New Rules
1. **Detekt**: Edit `detekt.yml`
2. **SwiftLint**: Edit `iosApp/.swiftlint.yml`

### Adding New Tools
1. Add the tool configuration to `verification.gradle.kts`
2. Update the `checkVerificationTools` task
3. Add the tool to the `lintAll` task dependencies

## Troubleshooting

### SwiftLint Not Found
```bash
# Check if SwiftLint is installed
which swiftlint

# Install if missing
brew install swiftlint
```

### Detekt Issues
```bash
# Run with more verbose output
./gradlew detekt --info

# Check Detekt configuration
cat config/detekt/detekt.yml
```
