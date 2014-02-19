import sbt._
import Keys._
import Project.Initialize

object AppBuild extends Build {
  import Dependencies._
  import Resolvers._

  lazy val buildSettings = Seq(
    organization  := "com.oracle.tools",
    organizationHomepage := Some(url("http://java.net/projects/oracletools")),
    version       := "1.2.2-SNAPSHOT",
    description   := "Oracle Tools"
  )

  override lazy val settings = super.settings ++ buildSettings

  lazy val baseSettings = Defaults.defaultSettings ++ Seq(
    javacOptions in Compile := Seq(
      "-encoding", "UTF-8",
      "-source", "1.7",
      "-target", "1.7",
      "-Xlint:all"),
    javacOptions in (Compile, doc) := Seq(
      "-encoding", "UTF-8",
      "-source", "1.7",
      "-quiet",
      "-notimestamp",
      "-linksource"),
    autoScalaLibrary := false,
    crossPaths := false,
    fork := true,
    resolvers ++= Seq(mavenLocal, jvisualvm)
  )

  lazy val parentSettings = baseSettings ++ Seq(
    publishArtifact in (Compile, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := false
  )

  lazy val defaultSettings = baseSettings ++ Seq(
  )

  lazy val nonRoots = projects.filter(_ != root).map(p => LocalProject(p.id))
  lazy val root: Project = Project(
    id        = "oracle-tools-all",
    base      = file("oracle-tools-all"),
    settings  = parentSettings,
    aggregate = nonRoots
  ) dependsOn(otCore, otRuntime, otTestingSupport, otCoherence, otCoherenceTestingSupport)

  lazy val otCore = Project(
    id        = "oracle-tools-core",
    base      = file("oracle-tools-core"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(cglib, objenesis,
        hamcrest % "test", mockito % "test", junit % "test", junitListener % "test")
    )
  )

  lazy val otTestingSupport = Project(
    id        = "oracle-tools-testing-support",
    base      = file("oracle-tools-testing-support"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(hamcrest, junit, junitListener % "test")
    )
  ) dependsOn(otCore, otRuntime)

  lazy val otRuntime = Project(
    id        = "oracle-tools-runtime",
    base      = file("oracle-tools-runtime"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(hamcrest % "test", mockito % "test",
        junit % "test", junitListener % "test")
    )
  ) dependsOn(otCore)

  lazy val otRuntimeTests = Project(
    id        = "oracle-tools-runtime-tests",
    base      = file("oracle-tools-runtime-tests"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(hamcrest % "test", mockito % "test",
        junit % "test", junitListener % "test")
    )
  ) dependsOn(otRuntime % "test", otTestingSupport % "test")

  lazy val otCoherence = Project(
    id        = "oracle-tools-coherence",
    base      = file("oracle-tools-coherence"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(coherence, hamcrest % "test", mockito % "test",
        junit % "test", junitListener % "test")
    )
  ) dependsOn(otRuntime, otTestingSupport % "test")

  lazy val otCoherenceTests = Project(
    id        = "oracle-tools-coherence-tests",
    base      = file("oracle-tools-coherence-tests"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(coherence % "test", junit % "test", junitListener % "test")
    )
  ) dependsOn(otCoherence % "test", otRuntime % "test", otTestingSupport % "test")

  lazy val otCoherenceTestingSupport = Project(
    id        = "oracle-tools-coherence-testing-support",
    base      = file("oracle-tools-coherence-testing-support"),
    settings  = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(coherence, junit, junitListener % "test")
    )
  ) dependsOn(otRuntime, otTestingSupport)

  lazy val otSite = Project(
    id        = "oracle-tools-site",
    base      = file("oracle-tools-site"),
    settings  = defaultSettings ++ Seq(
    )
  )
}

object Dependencies {

  val coherence = "com.oracle.coherence" % "coherence" % "3.7.1.11"
  val cglib     = "cglib" % "cglib" % "2.2.2"
  val objenesis = "org.objenesis" % "objenesis" % "1.2"
  val hamcrest  = "org.hamcrest" % "hamcrest-library" % "1.3"
  val mockito   = "org.mockito" % "mockito-all" % "1.9.0"
  val junit     = "junit" % "junit" % "4.11"
  val junitListener = "com.novocode" % "junit-interface" % "0.10"
}

object Resolvers {
  val jvisualvm = "jvisualvm" at "http://bits.netbeans.org/nexus/content/groups/visualvm/"
  val mavenLocal = "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
}
// vim: set ts=2 sw=2 et:
