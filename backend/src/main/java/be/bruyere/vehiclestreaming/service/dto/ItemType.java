package be.bruyere.vehiclestreaming.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@Getter(onMethod = @__({@JsonValue}))
@AllArgsConstructor
public enum ItemType {
    END15("TIMER"),
    VEHICLE("VEHICLE");

    private String value;

    @JsonCreator
    public static ItemType decode(final String value) {
        return Stream.of(ItemType.values())
            .filter(targetEnum -> targetEnum.value.equals(value))
            .findFirst()
            .orElse(null);
    }
}
