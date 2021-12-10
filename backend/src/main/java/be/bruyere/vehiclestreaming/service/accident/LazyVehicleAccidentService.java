package be.bruyere.vehiclestreaming.service.accident;

import be.bruyere.romain.eval.EvalExtension;
import be.bruyere.vehiclestreaming.algo.accident.LazyQREAccidentAlgo;
import be.bruyere.vehiclestreaming.service.ScheduleTaskService;
import be.bruyere.vehiclestreaming.service.dto.AccidentDto;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LazyVehicleAccidentService {


    private final ScheduleTaskService scheduleTaskService;
    private EvalExtension.StartEval<StreamingDto, Double> eval;

    private void configure(ParameterDto parameter) {
        System.out.println("Configured");
        eval = LazyQREAccidentAlgo.averageSpeedOfVehiclesBeforeLastAccident().start();
    }

    public Double getOutput() {
        if(eval.result().nonEmpty()) {
            return eval.result().get();
        } else {
            return null;
        }
    }

    public void start(ParameterDto parameters) {
        this.configure(parameters);
        this.startScheduler();
    }

    public void next(VehicleDto vehicle) {
        this.eval = eval.next(vehicle);
    }

    public void reset() {
        eval = null;
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
