import com.keivanabdi.tiny.TinyPlugin
import com.keivanabdi.tiny.TinyPlugin._

val tempFile = new File("tempFile")

val insideNotFailingTaskLogMessage: String = "(Inside notFailingTask)"
val insideFailingTaskLogMessage:    String = "(Inside failingTask)"

val onStartLogMessage:   String = "[onStart"
val onSuccessLogMessage: String = "[onSuccess]"
val onErrorLogMessage:   String = "[onError]"

lazy val root = (project in file("."))
  .enablePlugins(TinyPlugin)
  .settings(
    name := "test1",
    version := "0.1",
    scalaVersion := "2.12.8",
    TaskKey[Unit]("resetTempFile") := {
      if (tempFile.exists()) {
        tempFile.delete()
      }
    },
    TaskKey[Unit]("failingTask") := {
      IO.append(tempFile, insideFailingTaskLogMessage)
      val fail = 1 / 0
    },
    TaskKey[Unit]("notFailingTask") := {
      IO.append(tempFile, insideNotFailingTaskLogMessage)
    },
    commands += wrapCommand("failingTask")(
      onStart   = IO.append(tempFile, onStartLogMessage),
      onError   = IO.append(tempFile, onErrorLogMessage),
      onSuccess = IO.append(tempFile, onSuccessLogMessage)
    ),
    commands += wrapCommand("notFailingTask")(
      onStart   = IO.append(tempFile, onStartLogMessage),
      onError   = IO.append(tempFile, onErrorLogMessage),
      onSuccess = IO.append(tempFile, onSuccessLogMessage)
    ),
    TaskKey[Unit]("checkFailingTask") := {
      val tempFileContent = IO.read(tempFile)
      if (tempFileContent != s"""$onStartLogMessage$insideFailingTaskLogMessage$onErrorLogMessage""") {
        sys.error("Execution order does not match.")
      }
    },
    TaskKey[Unit]("checkNotFailingTask") := {
      val tempFileContent = IO.read(tempFile)
      if (tempFileContent != s"""$onStartLogMessage$insideNotFailingTaskLogMessage$onSuccessLogMessage""") {
        sys.error("Execution order does not match.")
      }
    },
  )
