package targets

import TargetSystem
import org.jsoup.Jsoup
import utils.*

class ConfluenceTargetImpl(override val url: String) : TargetSystem {
    override val targetName: String = "Confluence"
    override val cpe: String = "cpe:2.3:a:atlassian:confluence_server:-:*:*:*:*:*:*:*"

    override suspend fun check(): Resource<Boolean> {
        val preparedUrl = if (url.last() == '/') url.dropLast(1) else url

        try {
            val jsoupDoc = Jsoup.connect("$preparedUrl/login.action").get()
            return Resource.Success(jsoupDoc.select("#confluence-base-url").size != 0)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Some error occurred")
        }
        
    }

    override suspend fun version(): Resource<String> {
        val preparedUrl = if (url.last() == '/') url.dropLast(1) else url
        
        try {
            val jsoupDoc = Jsoup.connect("$preparedUrl/login.action").get()
            return Resource.Success(jsoupDoc.select("#footer-build-information").text())
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Some error occurred")
        }
        
    }
}