package com.mex.bidder.engine.ranking;

import com.google.inject.AbstractModule;
import com.mex.bidder.engine.ranking.impl.RandomWithTaPriceAdRanking;

/**
 * User: donghai
 * Date: 2016/11/20
 */
public class RankingModule extends AbstractModule {
    @Override
    protected void configure() {
//        bind(AdRanking.class).to(RandomAdRanking.class);
        bind(AdRanking.class).to(RandomWithTaPriceAdRanking.class);
    }
}
