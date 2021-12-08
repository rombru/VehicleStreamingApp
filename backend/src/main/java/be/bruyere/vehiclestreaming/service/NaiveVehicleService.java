package be.bruyere.vehiclestreaming.service;

import be.bruyere.vehiclestreaming.algo.tunnel.NaiveAlgo;
import be.bruyere.vehiclestreaming.service.dto.ParameterDto;
import be.bruyere.vehiclestreaming.service.dto.TimerDto;
import be.bruyere.vehiclestreaming.service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class NaiveVehicleService {

    private final ScheduleTaskService scheduleTaskService;
    private Double result;

    private void configure(ParameterDto parameter) {
        System.out.println("Configured");
        NaiveAlgo.configure(
            parameter.getLength(),
            parameter.getSlopeGrade(),
            parameter.getHabitualUseFactor());
    }

    public Double getOutput() {
        return result;
    }

    public void start(ParameterDto parameters) {
        this.configure(parameters);
        this.startScheduler();
    }

    public void next(VehicleDto vehicle) {
        result = NaiveAlgo.computeNextVehicle(vehicle);
        System.out.println(result);
    }

    public void reset() {
        scheduleTaskService.removeTaskFromScheduler(1);
    }

    public void startScheduler() {
        scheduleTaskService.addTaskToScheduler(
            1,
            () -> {
                System.out.println("Minute");
                result = NaiveAlgo.computeNextVehicle(new TimerDto());
            },
            Instant.now().plus(15, ChronoUnit.SECONDS),
            Duration.ofSeconds(10));
    }
}
