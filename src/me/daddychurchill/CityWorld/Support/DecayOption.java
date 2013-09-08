package me.daddychurchill.CityWorld.Support;

/**
 * Created with IntelliJ IDEA.
 * User: Thomas
 * Date: 07.09.13
 * Time: 11:35
 * To change this template use File | Settings | File Templates.
 */
public class DecayOption {

    private static final double defaultHoleScale = 1.0 / 20.0;
    private static final double defaultLeavesScale = 1.0 / 10.0;
    private static final double defaultFulldecay = 0.5D;
    private static final double defaultPartialdecay = 0.3D;
    private static final double defaultLeavesdecay = 0.1D;

    private static final DecayOption defaultOptions = new DecayOption(defaultHoleScale,defaultLeavesScale,defaultFulldecay,defaultPartialdecay,defaultLeavesdecay);

    /**
     * An instance of DecayOption with hardcoded default options
     * @return default decay options
     */
    public static DecayOption getDefaultDecayOptions(){
        return defaultOptions;
    }

    /* Decay Parameters */
    private double holeScale = 1.0 / 20.0;
    private double leavesScale = 1.0 / 10.0;
    private double fulldecay = 0.5D;
    private double partialdecay = fulldecay - 0.2D;
    private double leavesdecay = 0.1D;


    /**
     *
     * @param holeScale overall decay amplitude, default 1/20
     * @param leavesScale scale for leaves, default 1/10
     * @param fulldecay threshold for full decay, default 0.5
     * @param partialdecay threshold for partial decay, default 0.3
     * @param leavesdecay threshold for the addition of leaves around the decayed blocks, default 0.1D
     */
    public DecayOption(double holeScale, double leavesScale, double fulldecay, double partialdecay, double leavesdecay) {
        this.holeScale = holeScale;
        this.leavesScale = leavesScale;
        this.fulldecay = fulldecay;
        this.partialdecay = partialdecay;
        this.leavesdecay = leavesdecay;
    }

    public double getHoleScale() {
        return holeScale;
    }

    public DecayOption setHoleScale(double holeScale) {
        this.holeScale = holeScale;
        return this;
    }

    public double getLeavesScale() {
        return leavesScale;
    }

    public DecayOption setLeavesScale(double leavesScale) {
        this.leavesScale = leavesScale;
        return this;
    }

    public double getFulldecay() {
        return fulldecay;
    }

    public DecayOption setFulldecay(double fulldecay) {
        this.fulldecay = fulldecay;
        return this;
    }

    public double getPartialdecay() {
        return partialdecay;
    }

    public DecayOption setPartialdecay(double partialdecay) {
        this.partialdecay = partialdecay;
        return this;
    }

    public double getLeavesdecay() {
        return leavesdecay;
    }

    public DecayOption setLeavesdecay(double leavesdecay) {
        this.leavesdecay = leavesdecay;
        return this;
    }
}
