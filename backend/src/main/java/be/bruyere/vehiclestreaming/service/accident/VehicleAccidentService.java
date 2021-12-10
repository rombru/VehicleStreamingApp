package be.bruyere.vehiclestreaming.service.accident;

import StreamQRE.Eval;
import be.bruyere.vehiclestreaming.algo.accident.StreamQREAccidentAlgo;
import be.bruyere.vehiclestreaming.service.ScheduleTaskService;
import be.bruyere.vehiclestreaming.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VehicleAccidentService {

    private final ScheduleTaskService scheduleTaskService;
    private Eval<StreamingDto,Double> eval;

    private void configure(ParameterDto parameter) {
        System.out.println("Configured");
        eval = StreamQREAccidentAlgo.averageSpeedOfVehiclesBeforeLastAccident().getEval().start();
    }

    public Double getOutput() {
        return (Double) eval.getOutput();
    }

    public void start(ParameterDto parameters) {
        this.configure(parameters);
        this.startScheduler();
    }

    public void next(VehicleDto vehicle) {
        this.eval = eval.next(vehicle);
    }

    public void reset() {
        scheduleTaskService.removeTaskFromScheduler(1);
    }

    public void startScheduler() {
        scheduleTaskService.addTaskToScheduler(
            1,
            () -> {
                System.out.println("Accident");
                eval = eval.next(new AccidentDto());
            },
            Instant.now().plus(15, ChronoUnit.SECONDS),
            Duration.ofSeconds(10));
    }
}
