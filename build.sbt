name := "spark-streaming-phoenix"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.3.1",
  "org.apache.spark" %% "spark-sql" % "1.3.1",
  "org.apache.spark" %% "spark-streaming" % "1.3.1",
  "org.apache.spark" %% "spark-streaming-kafka" % "1.3.1"
)

// this is in ./lib for now
//"org.apache.phoenix" % "phoenix-client" % "4.4.0-HBase-0.98"

resolvers += Resolver.mavenLocal