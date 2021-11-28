package be.bruyere.vehiclestreaming.service.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TimerDto implements StreamingDto {
    @Override
    public ItemType getItemType() {
        return ItemType.END15;
    }
}
