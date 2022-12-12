package maestro.sniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class GameStopSnipe extends Snipe {


    public GameStopSnipe(String url) {
        this.url = url;
        this.itemName = parseItemName();
        this.users = new ArrayList<>();
    }


    @Override
    public boolean inStock() {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch(IOException e) {
            return false;
        }


        Elements elems = doc.select("div.my-6");
        for(Element e : elems.select("span")) {
            if(e.text().equalsIgnoreCase("Add to Cart"))
                return true;
        }

        return false;
    }

    @Override
    public String parseItemName() {
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch(IOException e) {
            return "ITEM_NAME_NOT_FOUND";
        }

        return doc.select("h2.font-display.font-bold.text-xl.tracking-1").first().text();
    }

    public static void main(String[] args) {
        Snipe gameStopSnipe = new GameStopSnipe("https://www.gamestop.com/consoles-hardware/nintendo-switch/consoles/products/nintendo-switch-lite-gray/206581.html");

        System.out.println(gameStopSnipe.parseItemName());
        System.out.println(gameStopSnipe.inStock());
    }
}
