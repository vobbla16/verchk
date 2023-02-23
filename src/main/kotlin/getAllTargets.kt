import targets.*
import kotlin.reflect.KClass

fun getAllTargets(): List<KClass<*>> {
//    val reflections = Reflections("com.vobbla16.vsosh-verchk")
//    return reflections.get(SubTypes.of(TargetSystem::class.java)).orEmpty()
    return listOf(
        GitlabTargetImpl::class,
        JenkinsTargetImpl::class,
        ConfluenceTargetImpl::class
    )
}