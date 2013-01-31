import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "playAppRest"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	"postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    	"org.apache.httpcomponents" % "httpcore" % "4.1.3",
    	"org.apache.httpcomponents" % "httpclient" % "4.1.2"
    )

    // Only compile the bootstrap bootstrap.less file and other *.less file in the stylesheets directory
    def customLessEntryPoints(base: File): PathFinder = (
		(base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
		(base / "app" / "assets" / "stylesheets" * "*.less")
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
    		lessEntryPoints <<= baseDirectory(customLessEntryPoints)
    )      
}
