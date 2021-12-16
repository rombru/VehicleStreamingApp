package be.bruyere.vehiclestreaming.service.tunnel;

import be.bruyere.romain.eval.EvalExtension;
import be.bruyere.vehiclestreaming.algo.accident.LazyQREAccidentAlgo;
import be.bruyere.vehiclestreaming.algo.tunnel.LazyQREAlgo;
import be.bruyere.vehiclestreaming.service.ScheduleTaskService;
import be.bruyere.vehiclestreaming.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LazyVehicleService {


    private final ScheduleTaskService scheduleTaskService;
    private EvalExtension.StartEval<StreamingDto, Double> eval;

    private void configure(ParameterDto parameter) {
        System.out.println("Configured");
        eval = LazyQREAlgo.computeLaneCapacity(
            parameter.getLength(),
            parameter.getSlopeGrade(),
            parameter.getHabitualUseFactor()).start();
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
                System.out.println("Minute");
                eval = eval.next(new TimerDto());
            },
            Instant.now().plus(15, ChronoUnit.SECONDS),
            Duration.ofSeconds(15));
    }
}
