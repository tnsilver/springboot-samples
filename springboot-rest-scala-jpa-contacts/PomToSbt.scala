import scala.xml._
// To convert a Maven pom.xml to build.sbt:
// You'll need JDK 1.8 so:
// export JAVA_HOME=/opt/java/jdk1.8.0_191
// 1) Place this code into a file called PomToSbt.scala next to pom.xml
// 2) Type scala PomToSbt.scala > build.sbt
// The dependencies from pom.xml will be extracted and place into a complete build.sbt file
// Because most pom.xml files only refernence non-Scala dependencies, I did not use %%
val lines = (XML.load("pom.xml") \\ "dependencies") \ "dependency" map { dependency => 
  val groupId = (dependency \ "groupId").text
  val artifactId = (dependency \ "artifactId").text
  val version = (dependency \ "version").text
  val scope = (dependency \ "scope").text
  val classifier = (dependency \ "classifier").text
  val artifactValName: String = artifactId.replaceAll("[-\\.]", "_")

  val scope2 = scope match {
    case "" => ""
    case _ => s""" % "$scope""""
  }

  s"""  "$groupId" %  "$artifactId" % "$version"$scope2"""
}

val buildSbt = io.Source.fromFile("config.sbt").mkString
val libText = "libraryDependencies ++= Seq("
val buildSbt2 = buildSbt.replace(libText, libText + lines.mkString("\n", ",\n", ""))
println(buildSbt2)

