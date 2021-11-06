import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {first} from "rxjs/operators";
import {VehicleModel} from "../models/vehicle.model";
import {ParametersModel} from "../models/parameters.model";
import {VehicleService} from "../services/vehicle.service";
import {LazyVehicleService} from "../services/lazy-vehicle.service";
import {AbstractVehicleService} from "../services/abstract-vehicle.service";
import {AlgoTypeEnum} from "../models/algo-type.enum";
import {NaiveVehicleService} from "../services/naive-vehicle.service";

@Injectable({
  providedIn: 'root'
})
export class AppState {

  private readonly parameters = new BehaviorSubject<ParametersModel>(null);
  public readonly parameters$ = this.parameters.asObservable();

  private readonly stop = new BehaviorSubject<boolean>(null);
  public readonly stop$ = this.stop.asObservable();

  private service: AbstractVehicleService;

  constructor(
    private vehicleService: VehicleService,
    private lazyVehicleService: LazyVehicleService,
    private naiveVehicleService: NaiveVehicleService
  ) {
  }

  public setServiceType(type: AlgoTypeEnum) {
    switch (type) {
      case AlgoTypeEnum.LAZYQRE:
        this.service = this.lazyVehicleService;
        break;
      case AlgoTypeEnum.STREAMQRE:
        this.service = this.vehicleService;
        break;
      case AlgoTypeEnum.NAIVE:
        this.service = this.naiveVehicleService;
        break;
    }
  }

  public setParameters(parameters: ParametersModel) {
    this.parameters.next(parameters);
  }

  public setStop(stop: boolean) {
    this.stop.next(stop);
  }

  public getOutput(): Observable<number> {
    return this.service.getOutput();
  }

  public start(param: ParametersModel): Observable<void> {
    return this.service.start(param);
  }

  public next(vehicle: VehicleModel): Observable<void> {
    return this.service.next(vehicle);
  }

  public reset(): Observable<void> {
    return this.service.reset();
  }
}
