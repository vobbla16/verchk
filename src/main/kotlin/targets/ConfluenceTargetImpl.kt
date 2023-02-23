package targets

import TargetSystem

class ConfluenceTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Confluence"
    override val cpe: String = "cpe:2.3:a:atlassian:confluence_server:-:*:*:*:*:*:*:*"

    override fun check(): Boolean {
        return false
    }

    override fun version(): String {
        return url + "1.1.1"
    }
}