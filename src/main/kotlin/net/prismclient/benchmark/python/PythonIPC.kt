package net.prismclient.benchmark.python

import java.io.File

abstract class PythonIPC(val scriptPath: File) {
    lateinit var processBuilder: ProcessBuilder

    open var interpreter = "python3"

    open fun initialize() {
        processBuilder = ProcessBuilder(listOf(interpreter, scriptPath.path))


    }

    open fun establishConnection() {}
}