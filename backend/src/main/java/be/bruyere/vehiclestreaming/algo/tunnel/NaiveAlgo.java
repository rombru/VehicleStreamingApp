package be.bruyere.vehiclestreaming.algo.tunnel;

import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;

import java.util.Objects;

import static be.bruyere.vehiclestreaming.service.dto.ItemType.VEHICLE;
import static be.bruyere.vehiclestreaming.service.dto.VehicleType.TRUCK;

public class NaiveAlgo {

    private static final EquivalenceFactorMatrix matrix = new EquivalenceFactorMatrix();

    private static Double total = 0D;
    private static Double totalTrucks = 0D;
    private static Double length = 0D;
    private static Double slopeGrade = 0D;
    private static Double habitualUseFactor = 0D;


    private static Integer indexVehicleBy1Hour = 0;
    private static Double[] vehicleBy1Hour = {0D,0D,0D,0D,0D};
    private static Integer indexVehicleBy15Min = 0;
    private static Double[] vehicleBy15Min = {0D, 0D};
    private static Double totalBy1Hour = null;
    private static Double totalBy15Min = null;
    private static Double speedSum = 0D;

    /**
     * Configuration of the parameters for the algorithm
     * @param length the road length
     * @param slopeGrade the slope grade
     * @param habitualUseFactor the habitual use factor
     */
    public static void configure(Double length, Double slopeGrade, Double habitualUseFactor) {
        NaiveAlgo.length = length;
        NaiveAlgo.slopeGrade = slopeGrade;
        NaiveAlgo.habitualUseFactor = habitualUseFactor;

        NaiveAlgo.indexVehicleBy1Hour = 0;
        NaiveAlgo.vehicleBy1Hour = new Double[]{0D, 0D, 0D, 0D, 0D};
        NaiveAlgo.indexVehicleBy15Min = 0;
        NaiveAlgo.vehicleBy15Min = new Double[]{0D, 0D};

        NaiveAlgo.totalBy1Hour = null;
        NaiveAlgo.totalBy15Min = null;

        NaiveAlgo.speedSum = 0D;
    }

    /**
     * Determine the lane capacity in tunnels with cars per hour per lane
     * @param item the next item
     * @return the capacity
     */
    public static Double computeNextVehicle(StreamingDto item) {
        if(Objects.equals(item.getItemType(),VEHICLE)) {
            total = total + 1;
            vehicleBy1Hour[indexVehicleBy1Hour] = vehicleBy1Hour[indexVehicleBy1Hour] + 1;
            vehicleBy15Min[indexVehicleBy15Min] = vehicleBy15Min[indexVehicleBy15Min] + 1;

            var vehicle = (VehicleDto)item;
            speedSum = speedSum + vehicle.getSpeed();
            if(Objects.equals(vehicle.getType(),TRUCK)) {
                totalTrucks = totalTrucks + 1;
            }
        } else {
            if(indexVehicleBy1Hour < 4) {
                indexVehicleBy1Hour++;
            } else {
                vehicleBy1Hour[0] = vehicleBy1Hour[1];
                vehicleBy1Hour[1] = vehicleBy1Hour[2];
                vehicleBy1Hour[2] = vehicleBy1Hour[3];
                vehicleBy1Hour[3] = vehicleBy1Hour[4];
                vehicleBy1Hour[4] = 0D;
                totalBy1Hour = vehicleBy1Hour[0] + vehicleBy1Hour[1] + vehicleBy1Hour[2] + vehicleBy1Hour[3];
            }
            if(indexVehicleBy15Min < 1) {
                indexVehicleBy15Min++;
            } else {
                vehicleBy15Min[0] = vehicleBy15Min[1];
                vehicleBy15Min[1] = 0D;
                if(totalBy15Min == null || totalBy15Min < vehicleBy15Min[0]) {
                    totalBy15Min = vehicleBy15Min[0];
                }
            }
        }

        var percentage = totalTrucks / total;
        var heavyVehicleFactor = 1.0 / (1.0 + percentage * (matrix.getCoefficient(length, slopeGrade, percentage) - 1.0));
        var averageSpeed = speedSum / total ;
        var theoreticalCapacityPerTrafficLane = averageSpeed * 10.0 + 1200.0;

        if(totalBy1Hour != null && totalBy15Min != null) {
            var peakHourFactor = totalBy1Hour / (4 * totalBy15Min);
            return theoreticalCapacityPerTrafficLane * peakHourFactor * heavyVehicleFactor * habitualUseFactor;
        } else {
            return null;
        }
    }
}
