package targets

import TargetSystem
import utils.*

class JenkinsTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Jenkins"
    override val cpe: String = "cpe:2.3:a:jenkins:jenkins:-:*:*:*:-:*:*:*"

    override suspend fun check(): Resource<Boolean> {
        return Resource.Success(false)
    }

    override suspend fun version(): Resource<String> {
        return Resource.Success(url + "1.1.1")
    }
}