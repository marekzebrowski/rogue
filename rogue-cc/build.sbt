libraryDependencies := Seq(
"org.scala-lang" % "scala-reflect" % scalaVersion.value,
"joda-time"                % "joda-time"               % "2.1"        % "compile",
"org.joda"                 % "joda-convert"            % "1.2"        % "compile",
"org.mongodb"              % "mongo-java-driver"       % "2.12.5"     % "compile"
)

Seq(RogueBuild.defaultSettings: _*)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)