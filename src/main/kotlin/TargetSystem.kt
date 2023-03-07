import utils.*

interface TargetSystem {
    /**
     * Target name
     */
    val targetName: String

    /**
     * NVD CPE name
     */
    val cpe: String

    /**
     * Url to analyze
     */
    val url: String

    /**
     * Check is the url matches this target
     * @return is url matches target
     */
    suspend fun check(): Resource<Boolean>

    /**
     * Get target's version
     * @return target's version presented as String
     */
    suspend fun version(): Resource<String>
}