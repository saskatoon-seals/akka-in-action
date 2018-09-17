//---------------------------general project config---------------------------------

name := "akka-in-action"

version := "0.1"

scalaVersion := "2.12.6"

scalaSource in Compile := baseDirectory.value / "src"
scalaSource in Test := baseDirectory.value / "test"

//------------------------------------resolvers-------------------------------------

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
resolvers += Classpaths.typesafeReleases

//addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")

//---------------------------library dependencies------------------------------------

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.0"

libraryDependencies ++= {
  val akkaVersion = "2.4.19"
  Seq(
    "com.typesafe.akka"       %%  "akka-actor"   % akkaVersion,
    "com.typesafe.akka"       %%  "akka-slf4j"   % akkaVersion,
    "com.typesafe.akka"       %%  "akka-testkit" % akkaVersion   % "test",
    "org.scalatest"           %%  "scalatest"    % "3.0.0"       % "test"
  )
}

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test
)

libraryDependencies += "redis.clients" % "jedis" % "2.9.0"
