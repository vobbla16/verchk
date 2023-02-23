package targets

import TargetSystem

class GitlabTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Gitlab"
    override val cpe: String = "cpe:2.3:a:gitlab:gitlab:-:*:*:*:-:*:*:*"

    override fun check(): Boolean {
        return false
    }

    override fun version(): String {
        return url + "1.1.1"
    }
}