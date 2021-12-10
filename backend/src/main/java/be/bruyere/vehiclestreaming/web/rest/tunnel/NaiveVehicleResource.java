package be.bruyere.vehiclestreaming.web.rest.tunnel;

import be.bruyere.vehiclestreaming.service.tunnel.NaiveVehicleService;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle/naive")
@RequiredArgsConstructor
public class NaiveVehicleResource {

    private final NaiveVehicleService naiveVehicleService;

    @GetMapping("/output")
    public ResponseEntity<Double> getOutput() {
        return ResponseEntity.ok(naiveVehicleService.getOutput());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody ParameterDto parameter){
        naiveVehicleService.start(parameter);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/next")
    public ResponseEntity<Void> nextVehicle(@RequestBody VehicleDto vehicle) {
        naiveVehicleService.next(vehicle);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset")
    public ResponseEntity<Void> reset() {
        naiveVehicleService.reset();
        return ResponseEntity.ok().build();
    }
}
