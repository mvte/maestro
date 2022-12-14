package maestro.sniper;

public class SnipeFactory {

    public Snipe createSnipe(String urlString) {
        Snipe snipe;
        URLType url = URLType.getURLType(urlString);
        if(url == null)
            return null;

        switch(url) {
            case AMAZON:
                snipe = new AmazonSnipe(urlString);
                break;
            case BEST_BUY:
                snipe = new BestBuySnipe(urlString);
                break;
            case GAMESTOP:
                snipe = new GameStopSnipe(urlString);
                break;
            case RUTGERS:
                snipe = new RutgersSnipe(urlString);
                break;
            default:
                return null;
        }

        return snipe;
    }

}
