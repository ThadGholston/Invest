package com.investmobile.invest;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by thad on 11/10/15.
 */
public class FavoritesLoader {

    public ArrayList<Favorite> getFavorites(){
        Favorite goog = new Favorite();
        goog.symbol = "GOOG";
        goog.type = FavoriteTypeEnum.STOCK;
        Favorite aapl = new Favorite();
        aapl.symbol = "AAPL";
        aapl.type = FavoriteTypeEnum.STOCK;
        Favorite msft = new Favorite();
        msft.type = FavoriteTypeEnum.STOCK;
        msft.symbol = "MSFT";
        Favorite spy = new Favorite();
        spy.symbol = "SPY";
        spy.type = FavoriteTypeEnum.MUTUAL_FUND;
        Favorite vfiax = new Favorite();
        vfiax.symbol = "VFIAX";
        vfiax.type = FavoriteTypeEnum.MUTUAL_FUND;
        Favorite vtsax = new Favorite();
        vtsax.symbol = "VTSAX";
        vtsax.type = FavoriteTypeEnum.MUTUAL_FUND;
        ArrayList<Favorite> favorites = new ArrayList<>();
        favorites.add(goog);
        favorites.add(aapl);
        favorites.add(msft);
        favorites.add(spy);
        favorites.add(vfiax);
        favorites.add(vtsax);
        return favorites;
    }
}
