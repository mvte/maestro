package maestro.sniper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class BestBuySnipe extends Snipe{

    public BestBuySnipe(String url) {
        this.url = url;
        this.itemName = parseItemName();
        this.users = new ArrayList<>();
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

        Elements elems = doc.select("div.fulfillment-add-to-cart-button");

        for (Element elem : elems) {
            String elemText = elem.text();
            if (elemText.contains("Add to Cart") || elemText.contains("Check Stores")) {
                return true;
            }
        }

        return false;
    }

    public String parseItemName() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return "ITEM_NAME_MISSING";
        }

        Elements elem = doc.select("div.sku-title");
        return(elem.get(0).text());
    }


}
