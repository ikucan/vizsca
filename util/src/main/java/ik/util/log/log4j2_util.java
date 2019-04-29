package ik.util.log;

import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * a wrapper around some log4j2 APIs which the scala compiler is struggling to
 * invoke
 */
public class log4j2_util {

  /**
   * helper function for creating a custom log4j appender on the fly scala
   * compiler seems to have issues with java generics
   * 
   * a file appender is created for a specific category and a pattern. it is
   * configured as a non-
   * 
   * @param fnm
   * @param nm
   * @param pttrn
   */
  public static void fle_appndr(String fnm, String nm, String pttrn, String ctgry) {
    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    final Configuration cfg = ctx.getConfiguration();

    final PatternLayout lyout = PatternLayout.newBuilder().withPattern(pttrn).build();
    final Appender appndr = FileAppender.newBuilder().withFileName(fnm).withName(nm).withLayout(lyout).withAppend(false).build();
    appndr.start();
    cfg.addAppender(appndr);
    AppenderRef ref = AppenderRef.createAppenderRef(nm, null, null);
    AppenderRef[] refs = new AppenderRef[] { ref };
    LoggerConfig lggr_cfg = LoggerConfig.createLogger(false, Level.TRACE, ctgry, "true", refs, null, cfg, null);
    lggr_cfg.addAppender(appndr, null, null);
    cfg.addLogger(ctgry, lggr_cfg);
    ctx.updateLoggers();
  }

  public static void fle_appndr(String fnm, String pttrn, String ctgry) {
    final String nm = "nm_" + Math.abs(new Random().nextInt(0x0fffffff));
    fle_appndr(fnm, nm, pttrn, ctgry);
  }
}
