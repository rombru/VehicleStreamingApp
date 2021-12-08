package be.bruyere.vehiclestreaming.algo.accident;

import be.bruyere.romain.qre.*;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;

import java.util.Objects;

import static be.bruyere.vehiclestreaming.service.dto.ItemType.ACCIDENT;
import static be.bruyere.vehiclestreaming.service.dto.ItemType.VEHICLE;

public class LazyQREAccidentAlgo {

    public static SplitQRE<StreamingDto, Double, Double, Double, Double> averageSpeedOfVehiclesBeforeLastAccident() {
        var isAccidentToken = new AtomQRE<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),ACCIDENT), x -> 0D);
        var isVehicleToken = new AtomQRE<StreamingDto,VehicleDto>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> (VehicleDto) x);
        var isVehicleSpeedToken = new ApplyQRE<>(isVehicleToken, x -> {
            GenerateEncryptionSimulation.generate();
            return x.getSpeed().doubleValue();
        });
        var isVehicleSpeedOrAccidentToken = new ElseQRE<>(isAccidentToken,isVehicleSpeedToken);
        var is10ItemsSpeedSum = new WindowQRE<>(isVehicleSpeedOrAccidentToken, 0D, Double::sum, x -> x, 10);
        var is10ItemsAvgSpeedBeforeAccident = new SplitQRE<>(is10ItemsSpeedSum, isAccidentToken, (x,y) -> x/10D, x -> x);
        var isVehicleSpeedSum = new IterQRE<>(isVehicleToken, 0D, (x,y) -> 0D, x -> x);
        return new SplitQRE<>(is10ItemsAvgSpeedBeforeAccident, isVehicleSpeedSum, (x,y) -> x, x -> x);
    }
}
