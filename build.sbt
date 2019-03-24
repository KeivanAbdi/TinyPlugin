import sbt.plugins.SbtPlugin
import ScriptedPlugin.autoImport._

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "tiny-plugin",
    version := "0.1-SNAPSHOT",
    organization:="com.keivanabdi",
    scalaVersion := "2.12.8",
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false
  )
