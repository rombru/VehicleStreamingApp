package be.bruyere.vehiclestreaming.service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class VehicleDto implements StreamingDto {
    private Long id;
    private Long speed;
    private VehicleType type;

    @Override
    public ItemType getItemType() {
        return ItemType.VEHICLE;
    }
}
