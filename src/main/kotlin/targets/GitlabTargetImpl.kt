package targets

import TargetSystem
import kotlinx.coroutines.delay

class GitlabTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Gitlab"
    override val cpe: String = "cpe:2.3:a:gitlab:gitlab:-:*:*:*:-:*:*:*"

    override suspend fun check(): Boolean {
        delay(2000)
        return false
    }

    override suspend fun version(): String {
        return url + "1.1.1"
    }
}