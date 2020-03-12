package tests;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

class JULOutputStream extends OutputStream {
    private final Logger logger;
    private final Level level;
    private final StringBuilder stringBuilder;

    JULOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public final void write(int i) {
        char c = (char) i;
        if (c == '\r' || c == '\n') {
            if (stringBuilder.length() > 0) {
                logger.log(level, stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        } else { stringBuilder.append(c); }
    }
}