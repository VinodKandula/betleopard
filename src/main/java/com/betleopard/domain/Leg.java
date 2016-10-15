package com.betleopard.domain;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author ben
 */
public class Leg {

    private final Race race;
    private final int oddsVersion;
    private final OddsType oType;
    private final Optional<Double> odds;
    private final Horse backing;
    private final Optional<Double> stakeOrAcc;

    public Leg(final long raceID, final long runnerID, final OddsType ot, final Double stake) {
        race = Race.of(raceID);
        backing = Horse.of(runnerID);
        oType = ot;
        if (oType == OddsType.FIXED_ODDS) {
            odds = Optional.of(race.currentOdds(backing));
            oddsVersion = race.version();
        } else {
            odds = Optional.empty();
            oddsVersion = -1;
        }
        stakeOrAcc = Optional.ofNullable(stake);
    }

    public Leg(final Race r, final Horse runner, final OddsType ot, final Double stake) {
        race = r;
        backing = runner;
        oType = ot;
        if (oType == OddsType.FIXED_ODDS) {
            odds = Optional.of(race.currentOdds(runner));
            oddsVersion = race.version();
        } else {
            odds = Optional.empty();
            oddsVersion = -1;
        }
        stakeOrAcc = Optional.ofNullable(stake);
    }

    private double stake() {
        if (!stakeOrAcc.isPresent()) {
            throw new IllegalStateException("Leg " + toString() + " is not staked yet - part of an accumulator");
        }
        return stakeOrAcc.get();
    }

    public double odds() {
        return odds.orElse(race.currentOdds(backing));
    }

    public Race getRace() {
        return race;
    }

    public double payout() {
        final Optional<Horse> winner = race.getWinner();
        if (!winner.isPresent()) {
            throw new IllegalArgumentException("Race " + race.toString() + " has not been run");
        }
        final Horse fptp = winner.get();
        if (backing == fptp) {
            return stake() * odds();
        }
        return 0.0;
    }

    public double payout(final double startingStake) {
        final Optional<Horse> winner = race.getWinner();
        if (!winner.isPresent()) {
            throw new IllegalArgumentException("Race " + race.toString() + " has not been run");
        }
        final Horse fptp = winner.get();
        if (backing == fptp) {
            return startingStake * odds();
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Leg{" + "race=" + race + ", oddsVersion=" + oddsVersion + ", oType=" + oType + ", odds=" + odds + ", backing=" + backing + ", stakeOrAcc=" + stakeOrAcc + '}';
    }

    static Leg parseBlob(Map<String, ?> blob) {
        final OddsType type = OddsType.valueOf("" + blob.get("oType"));
        final Object o = blob.get("odds");
        if (o == null) {
            // Starting price
        } else {

        }

        return null;
    }

}
