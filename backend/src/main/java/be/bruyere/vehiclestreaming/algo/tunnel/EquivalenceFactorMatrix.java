package be.bruyere.vehiclestreaming.algo.tunnel;

public class EquivalenceFactorMatrix {

    private final Double[] r2_0_400 = new Double[]{1.5, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r2_400_800 = new Double[]{1.5, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r2_800_1200 = new Double[]{1.5, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r2_1200_1600 = new Double[]{2.0, 2.0, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r2_1600_2400 = new Double[]{3.0, 3.0, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r2_2400 = new Double[]{3.5, 3.0, 2.5, 2.5, 2.0, 2.0};

    private final Double[] r3_0_400 = new Double[]{1.5, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r3_400_800 = new Double[]{2.5, 2.0, 2.0, 2.0, 2.0, 1.5};
    private final Double[] r3_800_1200 = new Double[]{4.0, 3.5, 3.5, 3.0, 2.5, 2.0};
    private final Double[] r3_1200_1600 = new Double[]{5.5, 4.5, 4.0, 4.0, 3.5, 3.0};
    private final Double[] r3_1600_2400 = new Double[]{6.0, 5.0, 4.5, 4.0, 4.0, 3.0};
    private final Double[] r3_2400 = new Double[]{6.0, 5.0, 4.5, 4.5, 4.0, 3.0};

    private final Double[] r4_0_400 = new Double[]{1.5, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r4_400_800 = new Double[]{4.0, 3.5, 3.0, 3.0, 3.0, 2.5};
    private final Double[] r4_800_1200 = new Double[]{7.0, 6.0, 5.5, 5.0, 4.5, 4.0};
    private final Double[] r4_1200_1600 = new Double[]{8.0, 6.5, 6.0, 5.5, 4.0, 4.5};
    private final Double[] r4_1600_2400 = new Double[]{8.0, 7.0, 6.0, 6.0, 5.0, 5.0};
    private final Double[] r4_2400 = new Double[]{8.0, 7.0, 6.0, 6.0, 5.0, 5.0};

    private final Double[] r5_0_400 = new Double[]{2.0, 1.5, 1.5, 1.5, 1.5, 1.5};
    private final Double[] r5_400_800 = new Double[]{4.5, 4.0, 3.5, 3.0, 3.0, 2.5};
    private final Double[] r5_800_1200 = new Double[]{7.0, 6.0, 5.5, 5.0, 4.5, 4.0};
    private final Double[] r5_1200_1600 = new Double[]{9.0, 8.0, 7.0, 7.0, 6.0, 6.0};
    private final Double[] r5_1600_2400 = new Double[]{9.5, 8.0, 7.5, 7.0, 6.5, 6.0};
    private final Double[] r5_2400 = new Double[]{9.5, 8.0, 7.5, 7.0, 6.5, 6.0};

    public Double getCoefficient(
        Double length,
        Double slopeGrade,
        Double truckPercentage
    ) {
        if(truckPercentage < 4) {
            return 1.0;
        }
        if(slopeGrade < 2) {
            return 1.5;
        } else if(slopeGrade <= 3) {
            return getCoefficientByLength(length, truckPercentage, r2_0_400, r2_400_800, r2_800_1200, r2_1200_1600, r2_1600_2400, r2_2400);
        } else if(slopeGrade <= 4) {
            return getCoefficientByLength(length, truckPercentage, r3_0_400, r3_400_800, r3_800_1200, r3_1200_1600, r3_1600_2400, r3_2400);
        } else if(slopeGrade <= 5) {
            return getCoefficientByLength(length, truckPercentage, r4_0_400, r4_400_800, r4_800_1200, r4_1200_1600, r4_1600_2400, r4_2400);
        } else {
            return getCoefficientByLength(length, truckPercentage, r5_0_400, r5_400_800, r5_800_1200, r5_1200_1600, r5_1600_2400, r5_2400);
        }
    }

    private Double getCoefficientByLength(
        Double length,
        Double truckPercentage,
        Double[] r_0_400,
        Double[] r_400_800,
        Double[] r_800_1200,
        Double[] r_1200_1600,
        Double[] r_1600_2400,
        Double[] r_2400
    ) {
        if(length < 400) {
            return getCoefficientByTruckPercentage(truckPercentage, r_0_400);
        } else if(length < 800) {
            return getCoefficientByTruckPercentage(truckPercentage, r_400_800);
        } else if(length < 1200) {
            return getCoefficientByTruckPercentage(truckPercentage, r_800_1200);
        } else if(length < 1600) {
            return getCoefficientByTruckPercentage(truckPercentage, r_1200_1600);
        } else if(length < 2400) {
            return getCoefficientByTruckPercentage(truckPercentage, r_1600_2400);
        } else {
            return getCoefficientByTruckPercentage(truckPercentage, r_2400);
        }
    }

    private Double getCoefficientByTruckPercentage(
        Double truckPercentage,
        Double[] coefs
    ) {
        if(truckPercentage <= 4) {
            return coefs[0];
        } else if(truckPercentage <= 6) {
            return coefs[1];
        } else if(truckPercentage <= 8) {
            return coefs[2];
        } else if(truckPercentage <= 10) {
            return coefs[3];
        } else if(truckPercentage <= 15) {
            return coefs[4];
        } else {
            return coefs[5];
        }
    }
}
