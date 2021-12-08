package be.bruyere.vehiclestreaming.algo.tunnel;

import be.bruyere.romain.qre.*;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleType;

import java.util.Objects;

import static be.bruyere.vehiclestreaming.service.dto.ItemType.END15;
import static be.bruyere.vehiclestreaming.service.dto.ItemType.VEHICLE;

public class LazyQREAlgo {

    private static final EquivalenceFactorMatrix matrix = new EquivalenceFactorMatrix();

    /**
     * Determine the lane capacity in tunnels with cars per hour per lane
     * @param length the tunnel length
     * @param slopeGrade the slope grade
     * @param habitualUseFactor the habitual use factor of drivers
     * @return the computing QRE
     */
    public static Combine3QRE<StreamingDto, Double, Double, Double, Double> computeLaneCapacity(
        Double length,
        Double slopeGrade,
        Double habitualUseFactor
    ) {
        return new Combine3QRE<>(
            theoreticalCapacityPerTrafficLane(),
            peakHourFactor(),
            heavyVehicleFactor(length, slopeGrade),
            (TC, PHF, HVF) -> TC * PHF * HVF * habitualUseFactor
        );
    }

    /**
     * Compute the theoretical capacity in cars per hour per lane
     * Use the average speed of vehicles in km/h
     * @return the QRE
     */
    private static ApplyQRE<StreamingDto, Double, Double> theoreticalCapacityPerTrafficLane() {
        var isAverageSpeed = LazyQREAlgo.averageSpeedOfVehicles();
        return new ApplyQRE<>(isAverageSpeed, x -> x * 10.0 + 1200.0);
    }

    /**
     * Compute the average speed of vehicles
     * @return the QRE
     */
    private static CombineQRE<StreamingDto, Double, Double, Double> averageSpeedOfVehicles() {
        var is15MinToken = new AtomQRE<StreamingDto,Double>(x -> Objects.equals(x.getItemType(), END15), x -> 0D);

        var isVehicleSpeedToken = new AtomQRE<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> ((VehicleDto)x).getSpeed().doubleValue());
        var isSpeed = new ElseQRE<>(isVehicleSpeedToken, is15MinToken);
        var isSpeedSum = new IterQRE<>(isSpeed, 0D, Double::sum, x -> x);

        var isVehicleCountToken = new AtomQRE<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1D);
        var isCount = new ElseQRE<>(isVehicleCountToken, is15MinToken);
        var isCountSum = new IterQRE<>(isCount, 0D, Double::sum, x -> x);

        return new CombineQRE<>(isSpeedSum, isCountSum, (x, y) -> x/y);
    }

    /**
     * Compute the peak hour factor (PHF), which represents the relationship of the hourly intensity
     * in capacity divided by four times the maximum number of vehicles in a period of
     * fifteen minutes during peak hour.
     * @return the QRE
     */
    public static CombineQRE<StreamingDto, Double, Double, Double> peakHourFactor() {
        var isVehicleToken = new AtomQRE<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1D);
        var is15MinToken = new AtomQRE<StreamingDto, Double>(x -> Objects.equals(x.getItemType(), END15), x -> 1D);

        var sumOfVehicle = new IterQRE<>(isVehicleToken, 0D, Double::sum, x -> x);
        var sumOfVehiclesDuring15Min = new SplitQRE<>(sumOfVehicle, is15MinToken, (x, y) -> x, x -> x);

        var repeatOfSumOfVehiclesDuring15Min = new IterQRE<>(sumOfVehiclesDuring15Min, 0D, (x,y) -> x > y ? x : y, x -> x);
        var sumOfVehiclesDuringLast15Min = new SplitQRE<>(repeatOfSumOfVehiclesDuring15Min, sumOfVehicle, (x, y) -> x > y ? x : y, x -> x);

        var sumOfVehicleDuring1Hour = new WindowQRE<>(sumOfVehiclesDuring15Min, 0D, Double::sum, x -> x, 4);
        var sumOfVehiclesDuringLastHour = new SplitQRE<>(sumOfVehicleDuring1Hour, sumOfVehicle, (x, y) -> x, x -> x);

        return new CombineQRE<>(sumOfVehiclesDuringLastHour, sumOfVehiclesDuringLast15Min, (x, y) -> x / (4 * y));
    }

    /**
     * Compute the factor of heavy vehicles, which indicates the effect of the types of slow
     * vehicles on the flow of light vehicles.
     * It depends on the percentage of heavy vehicles
     * @param length the tunnel length
     * @param slopeGrade the slope grade
     * @return the QRE
     */
    public static ApplyQRE<StreamingDto, Double, Double> heavyVehicleFactor(
        Double length,
        Double slopeGrade
    ) {
        return new ApplyQRE<>(percentageOfTrucks(), x -> 1.0 / (1.0 + x * (matrix.getCoefficient(length, slopeGrade, x) - 1.0)));
    }

    /**
     * Compute the percentage of trucks
     * @return the QRE
     */
    public static CombineQRE<StreamingDto, Double, Long, Long> percentageOfTrucks() {
        var is15MinToken = new AtomQRE<StreamingDto,Long>(x -> Objects.equals(x.getItemType(), END15), x -> 0L);
        var isVehicleToken = new AtomQRE<StreamingDto,Long>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1L);
        var isVehicle = new ElseQRE<>(isVehicleToken, is15MinToken);
        var isVehicleSum = new IterQRE<>(isVehicle, 0L, Long::sum, x -> x);

        var isTruckToken = new AtomQRE<StreamingDto,Long>(x -> Objects.equals(x.getItemType(),VEHICLE) && ((VehicleDto)x).getType() == VehicleType.TRUCK, x -> 1L);
        var isNotTruckToken = new AtomQRE<StreamingDto,Long>(x -> !Objects.equals(x.getItemType(),VEHICLE) || ((VehicleDto)x).getType() != VehicleType.TRUCK, x -> 0L);
        var isTruck = new ElseQRE<>(isTruckToken, isNotTruckToken);
        var isTruckSum = new IterQRE<>(isTruck, 0L, Long::sum, x -> x);

        return new CombineQRE<>(isTruckSum, isVehicleSum, (x, y) -> x.doubleValue()/y.doubleValue());
    }
}
