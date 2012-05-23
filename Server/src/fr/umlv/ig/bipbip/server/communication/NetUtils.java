package fr.umlv.ig.bipbip.server.communication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class NetUtils {

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT);

    private NetUtils() {
    }

    public static void writeLine(SocketChannel sc, String line) throws IOException {
        if (!line.endsWith("\n")) {
            line = line + "\n";
        }
        sc.write(ByteBuffer.wrap(line.getBytes()));
    }

    public static SimpleDateFormat getDateformat() {
        return dateFormat;
    }
}
