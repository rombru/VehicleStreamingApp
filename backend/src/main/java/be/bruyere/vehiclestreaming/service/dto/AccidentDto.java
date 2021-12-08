package be.bruyere.vehiclestreaming.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccidentDto implements StreamingDto {
    @Override
    public ItemType getItemType() {
        return ItemType.ACCIDENT;
    }
}
