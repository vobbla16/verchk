package targets

import TargetSystem
import org.jsoup.Jsoup

class ConfluenceTargetImpl(override val url: String) : TargetSystem {
    override val targetName: String = "Confluence"
    override val cpe: String = "cpe:2.3:a:atlassian:confluence_server:-:*:*:*:*:*:*:*"

    override suspend fun check(): Boolean {
        val preparedUrl = if (url.last() == '/') url.dropLast(1) else url

        val jsoupDoc = Jsoup.connect("$preparedUrl/login.action").get()
        return jsoupDoc.select("#confluence-base-url").size != 0
    }

    override suspend fun version(): String {
        val preparedUrl = if (url.last() == '/') url.dropLast(1) else url

        val jsoupDoc = Jsoup.connect("$preparedUrl/login.action").get()
        return jsoupDoc.select("#footer-build-information").text()
    }
}