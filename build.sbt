name := "akka-in-action"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.3")

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.0"

// https://mvnrepository.com/artifact/org.clojars.mmcgrana/java-beanstalk-client
//libraryDependencies += "org.clojars.mmcgrana" % "java-beanstalk-client" % "1.4.4"

/*
// https://mvnrepository.com/artifact/org.scalaz/scalaz-core
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.0-M24"
// https://mvnrepository.com/artifact/org.scalaz/scalaz-ioeffect
libraryDependencies += "org.scalaz" %% "scalaz-ioeffect" % "2.10.1"

// https://mvnrepository.com/artifact/org.mockito/mockito-core
libraryDependencies += "org.mockito" % "mockito-core" % "2.19.1" % Test
*/