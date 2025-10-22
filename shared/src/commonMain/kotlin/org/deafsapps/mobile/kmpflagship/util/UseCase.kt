package org.deafsapps.mobile.kmpflagship.util

/**
 * Base interface for all use cases in the application.
 * Use cases represent single business actions and should be lightweight.
 */
interface UseCase<in Input, out Output> {
    /**
     * Executes the use case with the given input and returns the result.
     * 
     * @param input The input parameters for the use case
     * @return The result of the use case execution
     */
    suspend operator fun invoke(input: Input): Output
}
