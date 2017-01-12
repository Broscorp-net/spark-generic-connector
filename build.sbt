val `testSparkVersion_1x` = settingKey[String]("The version of Spark to test against.")
val `testSparkVersion_2x` = settingKey[String]("The version of Spark to test against.")

val `defaultSparkVersion_1x` = settingKey[String]("The default version of Spark 1.x")
val `defaultSparkVersion_2x` = settingKey[String]("The default version of Spark 2.x")

val sparkVersion = settingKey[String]("The version of Spark")

lazy val commonSettings = Seq(
  organization := "es.alvsanand",
  name := "gdc-main",
  version := "0.2.0-SNAPSHOT",

  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.8"),

  `defaultSparkVersion_1x` := "1.6.0",
  `defaultSparkVersion_2x` := "2.1.0",

  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % scalaVersion.value % "provided",

    "org.scalatest" %% "scalatest" % "2.2.1" % "provided",
    "org.mockito" % "mockito-core" % "1.10.19" % "provided"),

  // Exlcude Sclala libraries in assembly
  assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
  assemblyExcludedJars in assembly := {
    val cp = (fullClasspath in assembly).value
    cp filter {_.data.getName.matches("scalatest.*")}
  },

  publishMavenStyle := true,

  parallelExecution in ThisBuild := false,

  // Skip tests during assembly
  test in assembly := {},

  ScoverageSbtPlugin.ScoverageKeys.coverageHighlighting := {
    if (scalaBinaryVersion.value == "2.10") false
    else true
  },
  ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "",

  // publishTo := {
  //  val nexus = "https://oss.sonatype.org/"
  //  if (version.value.endsWith("SNAPSHOT")) {
  //    Some("snapshots" at nexus + "content/repositories/snapshots")
  //  }
  //  else {
  //    Some("releases" at nexus + "service/local/staging/deploy/maven2")
  //  }
  // },

  pomExtra := (
    <url>https://github.com/alvsanand/spark-generic-downloader-connector</url>
      <licenses>
        <license>
          <name>Apache License, Version 2.0</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:alvsanand/spark-generic-downloader-connector.git</url>
        <connection>scm:git:git@github.com:alvsanand/spark-generic-downloader-connector
          .git</connection>
      </scm>
      <developers>
        <developer>
          <id>alvsanand</id>
          <name>Alvaro Santos Andres</name>
        </developer>
      </developers>)
)

lazy val `gdc-core` = (project in file("gdc-core")).
  settings(commonSettings: _*).
  settings(
    name := "gdc-core",

    libraryDependencies ++= Seq(
      ("com.wix" %% "accord-core" % "0.6.1").exclude("org.scala-lang", "scala-library"),

      "org.slf4j" % "slf4j-api" % "1.7.16",
      "org.slf4j" % "slf4j-log4j12" % "1.7.16",
      "log4j" % "log4j" % "1.2.16",
      "commons-io" % "commons-io" % "2.4",

      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided",
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided",

      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "test" classifier "tests",
      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "test" classifier "tests"
    )
  ).dependsOn()

lazy val `gdc-spark_1x` = (project in file("gdc-spark_1x")).
  settings(commonSettings: _*).
  settings(
    name := "gdc-spark_1x",

    sparkVersion := `defaultSparkVersion_1x`.value,
    `testSparkVersion_1x` := sys.props.get("spark.testVersion_1x").getOrElse(sparkVersion.value),

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % `testSparkVersion_1x`.value % "provided",
      "org.apache.spark" %% "spark-streaming" % `testSparkVersion_1x`.value % "provided",

      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet"),
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet")
    ),

    unmanagedSourceDirectories in Compile +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/main/scala",
    unmanagedSourceDirectories in Test +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/test/scala",
    unmanagedResourceDirectories in Compile +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/main/resources",
    unmanagedResourceDirectories in Test +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/test/resources"
  ).dependsOn(`gdc-core`)

lazy val `gdc-spark_2x` = (project in file("gdc-spark_2x")).
  settings(commonSettings: _*).
  settings(
    name := "gdc-spark_2x",

    sparkVersion := `defaultSparkVersion_2x`.value,
    `testSparkVersion_2x` := sys.props.get("spark.testVersion_2x").getOrElse(sparkVersion.value),

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % `testSparkVersion_2x`.value % "provided",
      "org.apache.spark" %% "spark-streaming" % `testSparkVersion_2x`.value % "provided",

      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet"),
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet")
    ),

    unmanagedSourceDirectories in Compile +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/main/scala",
    unmanagedSourceDirectories in Test +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/test/scala",
    unmanagedResourceDirectories in Compile +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/main/resources",
    unmanagedResourceDirectories in Test +=
      baseDirectory.value.getParentFile() / "gdc-spark/src/test/resources"
  ).dependsOn(`gdc-core`)

lazy val `gdc-google` = (project in file("gdc-google")).
  settings(commonSettings: _*).
  settings(
    name := "gdc-google",

    libraryDependencies ++= Seq(
      "com.google.api-client" % "google-api-client-java6" % "1.22.0",
      "com.google.apis" % "google-api-services-storage" % "v1-rev86-1.22.0",
      "com.google.http-client" % "google-http-client-jackson2" % "1.22.0",
      "com.google.oauth-client" % "google-oauth-client-jetty" % "1.22.0",

      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet"),
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet")
    )
  ).dependsOn(`gdc-core`)

lazy val `gdc-ftp` = (project in file("gdc-ftp")).
  settings(commonSettings: _*).
  settings(
    name := "gdc-ftp",

    libraryDependencies ++= Seq(
      "commons-net" % "commons-net" % "3.5",
      "com.jcraft" % "jsch" % "0.1.54",

      "org.apache.ftpserver" % "ftpserver-core" % "1.1.0" % "test",
      "org.apache.sshd" % "sshd-core" % "1.3.0" % "test",

      "org.apache.hadoop" % "hadoop-common" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet"),
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0" % "provided"
        excludeAll ExclusionRule(organization = "javax.servlet")
    )
  ).dependsOn(`gdc-core`)

lazy val root = (project in file(".")).
  aggregate(`gdc-core`, `gdc-spark_1x`, `gdc-spark_2x`, `gdc-google`, `gdc-ftp`).
  settings(
    aggregate in update := false
  )
