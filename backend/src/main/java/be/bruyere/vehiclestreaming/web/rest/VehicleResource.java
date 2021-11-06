package be.bruyere.vehiclestreaming.web.rest;

import be.bruyere.vehiclestreaming.service.VehicleService;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle")
@RequiredArgsConstructor
public class VehicleResource {

    private final VehicleService vehicleService;

    @GetMapping("/output")
    public ResponseEntity<Double> getOutput() {
        return ResponseEntity.ok(vehicleService.getOutput());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody ParameterDto parameter){
        vehicleService.start(parameter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> nextVehicle(@RequestBody VehicleDto vehicle) {
        vehicleService.next(vehicle);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> reset() {
        vehicleService.reset();
        return ResponseEntity.ok().build();
    }
}
