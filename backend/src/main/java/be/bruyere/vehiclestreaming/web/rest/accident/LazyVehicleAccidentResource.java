package be.bruyere.vehiclestreaming.web.rest.accident;

import be.bruyere.vehiclestreaming.service.accident.LazyVehicleAccidentService;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-accident/lazy")
@RequiredArgsConstructor
public class LazyVehicleAccidentResource {

    private final LazyVehicleAccidentService lazyVehicleAccidentService;

    @GetMapping("/output")
    public ResponseEntity<Double> getOutput() {
        return ResponseEntity.ok(lazyVehicleAccidentService.getOutput());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody ParameterDto parameter){
        lazyVehicleAccidentService.start(parameter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> nextVehicle(@RequestBody VehicleDto vehicle) {
        lazyVehicleAccidentService.next(vehicle);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> reset() {
        lazyVehicleAccidentService.reset();
        return ResponseEntity.ok().build();
    }
}
