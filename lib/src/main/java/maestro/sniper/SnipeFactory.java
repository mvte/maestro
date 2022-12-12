package maestro.sniper;

public class SnipeFactory {

    public Snipe createSnipe(String urlString, long userId) {
        Snipe snipe;
        String urlType;
        URLType url = URLType.getURLType(urlString);

        if(url != null)
            urlType = url.name();
        else
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
            default:
                return null;
        }

        return snipe;
    }

}
