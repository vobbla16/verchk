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
    fun check(): Boolean

    /**
     * Get target's version
     * @return target's version presented as String
     */
    fun version(): String
}