package maestro.sniper;

public enum URLType {

    BEST_BUY ("https://www.bestbuy.com"),
    AMAZON ("https://www.amazon.com"),
    GAMESTOP ("https://www.gamestop.com");

    private String prefix;
    private URLType(String prefix) {
        this.prefix = prefix;
    }

    private String getPrefix() {
       return prefix;
    }

    public static URLType getURLType(String check) {
        for(URLType url : URLType.values()) {
            if(check.startsWith(url.getPrefix())) {
                return url;
            }
        }

        return null;
    }
}
