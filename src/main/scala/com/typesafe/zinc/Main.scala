/**
 * Copyright (C) 2012 Typesafe, Inc. <http://www.typesafe.com>
 */

package com.typesafe.zinc

import java.io.File
import sbt.Level
import xsbti.CompileFailed

/**
 * Command-line main class.
 */
object Main {
  def main(args: Array[String]): Unit = run(args, None)

  /**
   * Compile run. Current working directory can be provided (for nailed zinc).
   */
  def run(args: Array[String], cwd: Option[File]): Unit = {
    val startTime = System.currentTimeMillis

    val Parsed(rawSettings, residual, errors) = Settings.parse(args)

    // normalise relative paths to the current working directory (if provided)
    val settings = Settings.normalise(rawSettings, cwd)

    // if nailed then also set any system properties provided
    if (cwd.isDefined) Util.setProperties(settings.properties)

    val log = Util.logger(settings.quiet, settings.logLevel, settings.color)
    val isDebug = (!settings.quiet && settings.logLevel == Level.Debug)

    // bail out on any command-line option errors
    if (!errors.isEmpty) {
      for (error <- errors) log.error(error)
      log.error("See %s -help for information about options" format Setup.Command)
      sys.exit(1)
    }

    if (settings.version) Setup.printVersion()

    if (settings.help) Settings.printUsage()

    // analysis manipulation utilities
    if (settings.analysisUtil.run) {
      val exitCode = try {
        SbtAnalysis.runUtil(settings.analysisUtil, log)
      } catch {
        case e: Exception => log.error(e.getMessage)
        sys.exit(1)
      }
      sys.exit(0) // only run analysis utilities
    }

    // if there are no sources provided, print version and usage by default
    if (settings.sources.isEmpty) {
      if (!settings.version && !settings.help) {
        Setup.printVersion()
        Settings.printUsage()
        sys.exit(1)
      }
      sys.exit(0)
    }

    val setup = Setup(settings)
    val inputs = Inputs(settings)

    // check we have all necessary files
    if (!Setup.verify(setup, log)) {
      log.error("See %s -help for information about locating necessary files" format Setup.Command)
      sys.exit(1)
    }

    // warn about cache file location when under zinc cache dir
    if (inputs.cacheFile.getCanonicalPath startsWith Setup.zincCacheDir.getPath) {
      log.warn("Default cache file location not accessible. Using " + inputs.cacheFile.getPath)
    }

    if (isDebug) {
      val debug: String => Unit = log.debug(_)
      Setup.show(setup, debug)
      Inputs.show(inputs, debug)
      debug("Setup and Inputs parsed " + Util.timing(startTime))
    }

    // run the compile
    try {
      val compiler = Compiler(setup, log)
      log.debug("Zinc compiler = %s [%s]" format (compiler, compiler.hashCode.toHexString))
      compiler.compile(inputs, cwd)(log)
      log.info("Compile success " + Util.timing(startTime))
    } catch {
      case e: CompileFailed =>
        log.error("Compile failed " + Util.timing(startTime))
        sys.exit(1)
      case e: Exception =>
        if (isDebug) e.printStackTrace
        val message = e.getMessage
        if (message ne null) log.error(message)
        sys.exit(1)
    }
  }
}
