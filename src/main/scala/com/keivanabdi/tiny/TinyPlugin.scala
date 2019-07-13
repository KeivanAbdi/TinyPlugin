package com.keivanabdi.tiny

import sbt.Keys._
import sbt._
object TinyPlugin extends AutoPlugin {

  /**
    *
    * @param commandName name of the command it's going to wrap
    * @param onStart block of code to be executed at the start
    * @param onError block of code to be executed on command failure
    * @param onSuccess block of code to be executed after successful execution of command
    *
    * @return
    */
  def wrapCommand(
      commandName: String
  )(
      onStart:   => Unit = {},
      onError:   => Unit = {},
      onSuccess: => Unit = {}
  ): Command = {

    // WrapperCommandName will prepend an extra "ext_" to the main command name
    val wrapperCommandName = "tiny_" + commandName

    // Defining our wrapperCommand
    Command.command(wrapperCommandName) { inputState =>
      // Executing onStart block
      onStart

      // Executing the main command
      val postCommandExecutionState: State = Command.process(commandName, inputState)
      if (postCommandExecutionState.onFailure.isEmpty) {
        // Executing onError block
        onError
      } else {
        // Executing onSuccess block
        onSuccess
      }
      postCommandExecutionState
    }
  }

  object MacSounds {
    val basso     = "/System/Library/Sounds/Basso.aiff"
    val blow      = "/System/Library/Sounds/Blow.aiff"
    val bottle    = "/System/Library/Sounds/Bottle.aiff"
    val frog      = "/System/Library/Sounds/Frog.aiff"
    val funk      = "/System/Library/Sounds/Funk.aiff"
    val glass     = "/System/Library/Sounds/Glass.aiff"
    val hero      = "/System/Library/Sounds/Hero.aiff"
    val morse     = "/System/Library/Sounds/Morse.aiff"
    val ping      = "/System/Library/Sounds/Ping.aiff"
    val pop       = "/System/Library/Sounds/Pop.aiff"
    val purr      = "/System/Library/Sounds/Purr.aiff"
    val sosumi    = "/System/Library/Sounds/Sosumi.aiff"
    val submarine = "/System/Library/Sounds/Submarine.aiff"
    val tink      = "/System/Library/Sounds/Tink.aiff"
  }

  /**
    * Plays an audio file on macOs
    * @param audioFile path to audio file
    */
  def playAudio(audioFile: String): Unit = {
    import scala.sys.process._
    Seq(
      "afplay",
      s"$audioFile"
    ).!
  }

  /**
    * Reads a text via macOs text to speech engine
    * @param text
    */
  def say(text: String): Unit = {
    import scala.sys.process._
    s"say $text".!
  }

  /**
    * Opens(or brings it to front if it's already open) an App by it's name (case sensitive)
    * @param appName name of the app like Opera,VLC, Spotify, etc
    * @return
    */
  def openApp(appName: String): Unit = {
    import scala.sys.process._
    Seq(
      "osascript",
      "-e",
      s"""tell application "$appName" to activate"""
    ).!
  }

  /**
    * Opens a url in the specified browser app
    * @param browserAppName name of the browser app
    * @param url requested url
    */
  def openUrl(browserAppName: String, url: String): Unit = {
    import scala.sys.process._
    Seq(
      "open",
      "-a",
      s"/Applications/$browserAppName.app",
      url
    ).!
  }

  /**
    * Reloads a tab in a chromium based browser
    * @param browserApp e.g.  "Google Chrome", "Opera", etc
    * @param which e.g. first, last, every
    * @param windowNumber number of window
    * @param tabAttributeKey e.g. title, url, etc
    * @param tabAttributeValue e.g localhost
    */
  def reloadBrowserTabs(
      browserApp:        String,
      which:             String,
      windowNumber:      Int = 1,
      tabAttributeKey:   String,
      tabAttributeValue: String
  ): Unit = {
    import scala.sys.process._
    Seq(
      "osascript",
      "-e",
      s"""tell application "$browserApp" to reload $which tab of window $windowNumber whose $tabAttributeKey contains "$tabAttributeValue""""
    ).!

  }

  /**
  * Switches browser tab
    * @param browserApp e.g.  "Google Chrome", "Opera", etc
    * @param windowNumber number of window
    * @param tabAttributeKey e.g. title, url, etc
    * @param tabAttributeValue  e.g localhost
    */
  def switchToTab(
      browserApp:        String,
      windowNumber:      Int = 1,
      tabAttributeKey:   String,
      tabAttributeValue: String
  ): Unit = {
    import scala.sys.process._
    Seq(
      "osascript",
      "-e",
      s"""
        |tell application "$browserApp"
        |	set i to 0
        |	repeat with t in (tabs of window $windowNumber)
        |		set i to i + 1
        |		if $tabAttributeKey of t contains "$tabAttributeValue" then
        |			set (active tab index of window $windowNumber) to i
        |		end if
        |	end repeat
        |end tell
      """.stripMargin
    ).!
  }

  sealed trait MediaCommand {
    val appleScriptMessage: String
  }

  object MediaCommand {
    case object Play extends MediaCommand {
      override val appleScriptMessage: String = "to play"
    }
    case object Pause extends MediaCommand {
      override val appleScriptMessage: String = "to pause"
    }
    case object Next extends MediaCommand {
      override val appleScriptMessage: String = "to next track"
    }
    case object Previous extends MediaCommand {
      override val appleScriptMessage: String =
        """
          | set player position to 0
          | previous track
          | end tell""".stripMargin
    }
    case class VolumeLevel(level: Int) extends MediaCommand {
      override val appleScriptMessage: String = s"to set sound volume to $level"
    }
  }

  def mediaControl(appName: String, mediaCommand: MediaCommand): Unit = {
    import scala.sys.process._
    Seq("osascript", "-e", s"""tell application "$appName" ${mediaCommand.appleScriptMessage}""").!
  }

}
