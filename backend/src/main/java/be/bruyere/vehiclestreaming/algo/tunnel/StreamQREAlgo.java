package be.bruyere.vehiclestreaming.algo.tunnel;

import StreamQRE.*;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleType;

import java.util.Objects;

import static be.bruyere.vehiclestreaming.service.dto.ItemType.END15;
import static be.bruyere.vehiclestreaming.service.dto.ItemType.VEHICLE;

public class StreamQREAlgo {

    private static final EquivalenceFactorMatrix matrix = new EquivalenceFactorMatrix();

    /**
     * Determine the lane capacity in tunnels with cars per hour per lane
     * @param length the tunnel length
     * @param slopeGrade the slope grade
     * @param habitualUseFactor the habitual use factor of drivers
     * @return the computing QRE
     */
    public static QReCombine<StreamingDto, Double, Double, Double> computeLaneCapacity(
        Double length,
        Double slopeGrade,
        Double habitualUseFactor
    ) {
        return new QReCombine<>(
            theoreticalCapacityPerTrafficLane(),
            new QReCombine<>(
                peakHourFactor(),
                heavyVehicleFactor(length, slopeGrade),
                (PHF, HVF) -> PHF * HVF * habitualUseFactor
            ),
            (TC, R) -> TC * R
        );
    }

    /**
     * Compute the theoretical capacity in cars per hour per lane
     * Use the average speed of vehicles in km/h
     * @return the QRE
     */
    private static QReApply<StreamingDto, Double, Double> theoreticalCapacityPerTrafficLane() {
        var isAverageSpeed = StreamQREAlgo.averageSpeedOfVehicles();
        return new QReApply<>(isAverageSpeed, x -> x * 10.0 + 1200.0);
    }

    /**
     * Compute the average speed of vehicles
     * @return the QRE
     */
    private static QReCombine<StreamingDto, Double, Double, Double> averageSpeedOfVehicles() {
        var is15MinToken = new QReAtomic<StreamingDto,Double>(x -> Objects.equals(x.getItemType(), END15), x -> 0D);

        var isVehicleSpeedToken = new QReAtomic<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> ((VehicleDto)x).getSpeed().doubleValue());
        var isSpeed = new QReElse<>(isVehicleSpeedToken, is15MinToken);
        var isSpeedSum = new QReIter<>(isSpeed, 0D, Double::sum, x -> x);

        var isVehicleCountToken = new QReAtomic<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1D);
        var isCount = new QReElse<>(isVehicleCountToken, is15MinToken);
        var isCountSum = new QReIter<>(isCount, 0D, Double::sum, x -> x);

        return new QReCombine<>(isSpeedSum, isCountSum, (x, y) -> x/y);
    }

    /**
     * Compute the peak hour factor (PHF), which represents the relationship of the hourly intensity
     * in capacity divided by four times the maximum number of vehicles in a period of
     * fifteen minutes during peak hour.
     * @return the QRE
     */
    private static QReCombine<StreamingDto, Double, Double, Double> peakHourFactor() {
        var isVehicleToken = new QReAtomic<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1D);
        var is15MinToken = new QReAtomic<StreamingDto, Double>(x -> Objects.equals(x.getItemType(), END15), x -> 1D);

        var sumOfVehicle = new QReIter<>(isVehicleToken, 0D, Double::sum, x -> x);
        var sumOfVehiclesDuring15Min = new QReSplit<>(sumOfVehicle, is15MinToken, (x, y) -> x);

        var repeatOfSumOfVehiclesDuring15Min = new QReIter<>(sumOfVehiclesDuring15Min, 0D, (x,y) -> x > y ? x : y, x -> x);
        var sumOfVehiclesDuringLast15Min = new QReSplit<>(repeatOfSumOfVehiclesDuring15Min, sumOfVehicle, (x, y) -> x > y ? x : y);

        var sumOfVehicleDuring1Hour = new QReWindow<>(sumOfVehiclesDuring15Min, 0D, Double::sum, x -> x, 4);
        var sumOfVehiclesDuringLastHour = new QReSplit<>(sumOfVehicleDuring1Hour, sumOfVehicle, (x, y) -> x);

        return new QReCombine<>(sumOfVehiclesDuringLastHour, sumOfVehiclesDuringLast15Min, (x, y) -> x / (4 * y));
    }

    /**
     * Compute the factor of heavy vehicles, which indicates the effect of the types of slow
     * vehicles on the flow of light vehicles.
     * It depends on the percentage of heavy vehicles
     * @param length the tunnel length
     * @param slopeGrade the slope grade
     * @return the QRE
     */
    private static QReApply<StreamingDto, Double, Double> heavyVehicleFactor(
        Double length,
        Double slopeGrade
    ) {
        return new QReApply<>(percentageOfTrucks(), x -> 1.0 / (1.0 + x * (matrix.getCoefficient(length, slopeGrade, x) - 1.0)));
    }

    /**
     * Compute the percentage of trucks
     * @return the QRE
     */
    private static QReCombine<StreamingDto, Long, Long, Double> percentageOfTrucks() {
        var is15MinToken = new QReAtomic<StreamingDto,Long>(x -> Objects.equals(x.getItemType(), END15), x -> 0L);
        var isVehicleToken = new QReAtomic<StreamingDto,Long>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> 1L);
        var isVehicleValue = new QReElse<>(isVehicleToken, is15MinToken);
        var isVehicleSum = new QReIter<>(isVehicleValue, 0L, Long::sum, x -> x);

        var isTruckToken = new QReAtomic<StreamingDto,Long>(x -> Objects.equals(x.getItemType(),VEHICLE) && ((VehicleDto)x).getType() == VehicleType.TRUCK, x -> 1L);
        var isNotTruckToken = new QReAtomic<StreamingDto,Long>(x -> !Objects.equals(x.getItemType(),VEHICLE) || ((VehicleDto)x).getType() != VehicleType.TRUCK, x -> 0L);
        var isTruckValue = new QReElse<>(isTruckToken, isNotTruckToken);
        var isTruckSum = new QReIter<>(isTruckValue, 0L, Long::sum, x -> x);

        return new QReCombine<>(isTruckSum, isVehicleSum, (x, y) -> x.doubleValue()/y.doubleValue());
    }
}
