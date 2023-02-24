package targets

import TargetSystem

class JenkinsTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Jenkins"
    override val cpe: String = "cpe:2.3:a:jenkins:jenkins:-:*:*:*:-:*:*:*"

    override suspend fun check(): Boolean {
        return false
    }

    override suspend fun version(): String {
        return url + "1.1.1"
    }
}