organization := "co.adhoclabs"

name := "ZioHttpUtils"

version := "0.0.4"

scalaVersion := "2.12.12"


resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  // External dependencies
  "ch.qos.logback"      %  "logback-classic"      % "1.2.3",
  "org.postgresql"      %  "postgresql"           % "42.2.24",
  "com.typesafe.slick"  %% "slick"                % "3.3.3",
  "com.typesafe.slick"  %% "slick-hikaricp"       % "3.3.3",
  "com.github.tminglei" %% "slick-pg"             % "0.19.7",
  "org.flywaydb"        %  "flyway-core"          % "7.15.0",

  // Our dependencies
  "co.adhoclabs" %% "model"      % "3.4.0",
  "co.adhoclabs" %% "secrets"    % "1.0.0",
  "co.adhoclabs" %% "sqs_client" % "3.3.1",

  // Test dependencies
  "org.scalatest"     %% "scalatest"           % "3.2.16"        % Test,
  "org.scalamock"     %% "scalamock"           % "5.2.0"         % Test,

  // ZIO-HTTP (Let's get away from akka!)
  "dev.zio" %% "zio-http" % "3.0.0-RC4+71-b1da91b6-SNAPSHOT",
  "dev.zio" %% "zio-http-testkit" % "3.0.0-RC4+71-b1da91b6-SNAPSHOT",
  "dev.zio" %% "zio-schema"          % "0.4.15",
)

// Scalariform preferences, described here: https://github.com/scala-ide/scalariform
//import scalariform.formatter.preferences._
//scalariformPreferences := scalariformPreferences.value
//  .setPreference(AlignArguments, true)
//  .setPreference(AlignParameters, true)
//  .setPreference(AlignSingleLineCaseStatements, true)
//  .setPreference(DanglingCloseParenthesis, Force)
//  .setPreference(FirstArgumentOnNewline, Force)
//  .setPreference(FirstParameterOnNewline, Force)
//  .setPreference(NewlineAtEndOfFile, true)
//  .setPreference(RewriteArrowSymbols, true)
//  .setPreference(SpacesAroundMultiImports, false)
//  .setPreference(UseUnicodeArrows, false)

// Prevents tests from executing when running 'sbt assembly' (prevents repetition in Circle)
test in assembly := {}

// Otherwise we get no logging from tests on secondary threads
parallelExecution in Test := false

// Use ScalaTest's log buffering to see test logs in the correct order
logBuffered in Test := false

// If running `sbt assembly` results in an error message containing:
//   java.lang.RuntimeException: deduplicate: different file contents found in the following:
// then implement a merge strategy like the one below (See https://github.com/sbt/sbt-assembly#merge-strategy for information):
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

publishTo := {
  val nexus = "https://nexusrepo.burnerapp.com/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "content/repositories/releases")
}
