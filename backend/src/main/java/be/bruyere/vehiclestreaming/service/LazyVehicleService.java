package be.bruyere.vehiclestreaming.service;

import be.bruyere.romain.eval.EvalExtension;
import be.bruyere.vehiclestreaming.algo.LazyAlgo;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.TimerDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
//        eval = LazyAlgo.percentageOfTrucks().start();
        eval = LazyAlgo.computeLaneCapacity(
            parameter.getLength(),
            parameter.getSlopeGrade(),
            parameter.getHabitualUseFactor()).start();
    }

    public Double getOutput() {
        System.out.println("Output");
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
        System.out.println(this.eval.result());
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
            Duration.ofSeconds(10));
    }
}
