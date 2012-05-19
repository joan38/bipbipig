package fr.umlv.ig.bipbip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Locale;

public final class NetUtils {

    private final static Charset charset = Charset.forName("UTF-8");
    private final static DateFormat dateFormat=DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,Locale.US);

    private NetUtils() {}
    
    public static void writeLine(SocketChannel sc,String line) throws IOException {
        if (!line.endsWith("\n")) {
            line=line+"\n";
        }
        sc.write(ByteBuffer.wrap(line.getBytes(charset)));
    }

    public static Charset getCharset() {
        return charset;
    }

    public static DateFormat getDateformat() {
        return dateFormat;
    }

}
