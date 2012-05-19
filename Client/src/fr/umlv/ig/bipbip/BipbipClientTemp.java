package fr.umlv.ig.bipbip;

import fr.umlv.ig.bipbip.poi.POI;
import fr.umlv.ig.bipbip.poi.POIType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class BipbipClientTemp {

    private final InetSocketAddress server;
    Charset charset = Charset.forName("UTF8");
    private SocketChannel sc;
    private Scanner scanner;

    public BipbipClientTemp(String host, int port) {
        this.server = new InetSocketAddress(host, port);
    }

    public void connect() throws IOException {
        sc = SocketChannel.open();
        sc.connect(server);
        scanner = new Scanner(sc, NetUtils.getCharset().name());
    }

    public void submit(POIType event, double x, double y, Date date) throws IOException {
        ClientCommand.submit(sc, event, x, y, date);
    }

    @SuppressWarnings("unchecked")
    public void getInfo(double x, double y) throws IOException {
        ClientCommand.getInfo(sc, x, y);
        if (!scanner.hasNext() || !scanner.next().equals(ServerCommandHandler.INFOS.name())) {
            throw new IOException("Server did not respond to the GET_INFO query");
        }
        ArrayList<POI> list = (ArrayList<POI>) ServerCommandHandler.INFOS.handle(sc, scanner);
        System.out.println("SERVER: INFOS " + list.size());
        for (POI e : list) {
            System.out.println("SERVER: INFO " + e.getType().name() + " " + x + " " + y);
        }
    }

    public void close() throws IOException {
        sc.close();
    }

    public static void main(String[] args) throws IOException {        
        
//        BipbipClient client = new BipbipClient("localhost", 6996);
//        client.connect();
//        client.submit(EventType.RADAR_FIXE, 35, 10, new Date());
//        client.submit(EventType.TRAVAUX, 122, -20, new Date());
//        client.getInfo(1, 23);
    }
}
