libraryDependencies := {
  val liftVersion = "2.6"
  val specsVersion = "2.4.2"
  Seq(
    "com.foursquare"           %% "rogue-field"     % "2.4.0"      % "compile",
    "net.liftweb"              %% "lift-mongodb"    % liftVersion  % "compile" intransitive(),
    "net.liftweb"              %% "lift-common"     % liftVersion  % "compile",
    "net.liftweb"              %% "lift-json"      % liftVersion  % "compile",
    "net.liftweb"              %% "lift-util"       % liftVersion  % "compile",
    "joda-time"                % "joda-time"           % "2.1"        % "provided",
    "org.joda"                 % "joda-convert"        % "1.2"        % "provided",
    "org.mongodb"              % "mongo-java-driver"   % "2.12.5"     % "compile",
    "junit"                    % "junit"               % "4.5"        % "test",
    "com.novocode"             % "junit-interface"     % "0.6"        % "test",
    "ch.qos.logback"           % "logback-classic"     % "0.9.26"     % "provided",
    "org.specs2"              %% "specs2"              % specsVersion % "test",
    "org.scala-lang"           % "scala-compiler"      % "2.11.7" % "test"
  )
}

Seq(RogueBuild.defaultSettings: _*)
