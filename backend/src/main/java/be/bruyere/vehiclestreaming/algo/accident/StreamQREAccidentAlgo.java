package be.bruyere.vehiclestreaming.algo.accident;

import StreamQRE.*;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;

import java.util.Objects;

import static be.bruyere.vehiclestreaming.service.dto.ItemType.ACCIDENT;
import static be.bruyere.vehiclestreaming.service.dto.ItemType.VEHICLE;

public class StreamQREAccidentAlgo {

    public static QReSplit<StreamingDto, Double, Double, Double> averageSpeedOfVehiclesBeforeLastAccident() {
        var isAccidentToken = new QReAtomic<StreamingDto,Double>(x -> Objects.equals(x.getItemType(),ACCIDENT), x -> 0D);
        var isVehicleToken = new QReAtomic<StreamingDto,VehicleDto>(x -> Objects.equals(x.getItemType(),VEHICLE), x -> (VehicleDto) x);
        var isVehicleSpeedToken = new QReApply<>(isVehicleToken, x -> {
            GenerateEncryptionSimulation.generate();
            return x.getSpeed().doubleValue();
        });
        var isVehicleSpeedOrAccidentToken = new QReElse<>(isAccidentToken,isVehicleSpeedToken);
        var is10ItemsSpeedSum = new QReWindow<>(isVehicleSpeedOrAccidentToken, 0D, Double::sum, x -> x, 10);
        var is10ItemsAvgSpeedBeforeAccident = new QReSplit<>(is10ItemsSpeedSum, isAccidentToken, (x,y) -> x/10D);
        var isVehicleSpeedSum = new QReIter<>(isVehicleToken, 0D, (x,y) -> 0D, x -> x);
        return new QReSplit<>(is10ItemsAvgSpeedBeforeAccident, isVehicleSpeedSum, (x, y) -> x);
    }
}
