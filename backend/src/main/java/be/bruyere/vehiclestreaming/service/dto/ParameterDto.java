package be.bruyere.vehiclestreaming.service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParameterDto {
    private Double length;
    private Double slopeGrade;
    private Double habitualUseFactor;
}
