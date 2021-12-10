package be.bruyere.vehiclestreaming.web.rest.accident;

import be.bruyere.vehiclestreaming.service.accident.VehicleAccidentService;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-accident")
@RequiredArgsConstructor
public class VehicleAccidentResource {

    private final VehicleAccidentService vehicleAccidentService;

    @GetMapping("/output")
    public ResponseEntity<Double> getOutput() {
        return ResponseEntity.ok(vehicleAccidentService.getOutput());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody ParameterDto parameter){
        vehicleAccidentService.start(parameter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> nextVehicle(@RequestBody VehicleDto vehicle) {
        vehicleAccidentService.next(vehicle);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> reset() {
        vehicleAccidentService.reset();
        return ResponseEntity.ok().build();
    }
}
