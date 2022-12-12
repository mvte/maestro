package maestro.sniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NikeSnipe extends Snipe {

    public NikeSnipe(String url) {
        this.url = url;
    }

    @Override
    public boolean inStock() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        Elements elems = doc.select("button[aria-label='Add to Bag']");

        return !elems.isEmpty();
    }

    @Override
    public String parseItemName() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return "ITEM_NAME_NOT_FOUND";
        }

        Element element = doc.select("h1#pdp_product_title").first();
        if(element == null) {
            return "ITEM_NAME_NOT_FOUND";
        } else {
            return element.text();
        }
    }

    public static void main(String[] args) {
        Snipe nikeSnipe = new NikeSnipe("https://www.nike.com/t/air-force-1-07-mens-shoes-5QFp5Z/CW2288-111");

        System.out.println(nikeSnipe.parseItemName());
        System.out.println(nikeSnipe.inStock());

    }
}
