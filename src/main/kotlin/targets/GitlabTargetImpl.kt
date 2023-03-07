package targets

import TargetSystem
import kotlinx.coroutines.delay
import utils.*

class GitlabTargetImpl(override val url: String): TargetSystem {
    override val targetName: String = "Gitlab"
    override val cpe: String = "cpe:2.3:a:gitlab:gitlab:-:*:*:*:-:*:*:*"

    override suspend fun check(): Resource<Boolean> {
        delay(2000)
        return Resource.Success(false)
    }

    override suspend fun version(): Resource<String> {
        return Resource.Success(url + "1.1.1")
    }
}