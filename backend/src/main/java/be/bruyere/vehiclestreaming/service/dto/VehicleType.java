package be.bruyere.vehiclestreaming.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter(onMethod = @__({@JsonValue}))
@AllArgsConstructor
public enum VehicleType {
    CAR("C"),
    TRUCK("T"),
    MOTORBIKE("M");

    private String value;

    @JsonCreator
    public static VehicleType decode(final String value) {
        return Stream.of(VehicleType.values())
            .filter(targetEnum -> targetEnum.value.equals(value))
            .findFirst()
            .orElse(null);
    }
}
