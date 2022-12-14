package maestro.sniper;

public enum URLType {

    BEST_BUY ("https://www.bestbuy.com"),
    AMAZON ("https://www.amazon.com"),
    GAMESTOP ("https://www.gamestop.com"),
    RUTGERS(" ");

    private final String prefix;
    URLType(String prefix) {
        this.prefix = prefix;
    }

    private String getPrefix() {
       return prefix;
    }

    public static URLType getURLType(String check) {
        if(check.matches("^\\d+$") && check.length() == 5) {
            return RUTGERS;
        }

        for(URLType url : URLType.values()) {
            if(check.startsWith(url.getPrefix())) {
                return url;
            }
        }

        return null;
    }
}
