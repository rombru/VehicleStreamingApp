package be.bruyere.vehiclestreaming.service;

import StreamQRE.Eval;
import be.bruyere.vehiclestreaming.algo.accident.StreamQREAccidentAlgo;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.StreamingDto;
import be.bruyere.vehiclestreaming.service.dto.TimerDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final ScheduleTaskService scheduleTaskService;
    private Eval<StreamingDto,Double> eval;

    private void configure(ParameterDto parameter) {
        System.out.println("Configured");
        eval = StreamQREAccidentAlgo.averageSpeedOfVehiclesBeforeLastAccident().getEval().start();
//        eval = StreamQREAlgo.computeLaneCapacity(
//            parameter.getLength(),
//            parameter.getSlopeGrade(),
//            parameter.getHabitualUseFactor()).getEval().start();
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
//        System.out.println(this.eval.getOutput());
    }

    public void reset() {
        scheduleTaskService.removeTaskFromScheduler(1);
    }

    public void startScheduler() {
        scheduleTaskService.addTaskToScheduler(
            1,
            () -> {
                eval = eval.next(new TimerDto());
            },
            Instant.now().plus(15, ChronoUnit.SECONDS),
            Duration.ofSeconds(10));
    }
}
