package be.bruyere.vehiclestreaming.web.rest.tunnel;

import be.bruyere.vehiclestreaming.service.tunnel.LazyVehicleService;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle/lazy")
@RequiredArgsConstructor
public class LazyVehicleResource {

    private final LazyVehicleService lazyVehicleService;

    @GetMapping("/output")
    public ResponseEntity<Double> getOutput() {
        return ResponseEntity.ok(lazyVehicleService.getOutput());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody ParameterDto parameter){
        lazyVehicleService.start(parameter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> nextVehicle(@RequestBody VehicleDto vehicle) {
        lazyVehicleService.next(vehicle);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> reset() {
        lazyVehicleService.reset();
        return ResponseEntity.ok().build();
    }
}
