package maestro.sniper;

public class AmazonSnipe extends Snipe{

    public AmazonSnipe(String url) {

    }

    @Override
    public boolean inStock() {
        return false;
    }

    @Override
    public String parseItemName() {
        return null;
    }
}
