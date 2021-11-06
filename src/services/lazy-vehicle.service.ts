import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {first} from "rxjs/operators";
import {VehicleModel} from "../models/vehicle.model";
import {AbstractVehicleService} from "./abstract-vehicle.service";
import {ParametersModel} from "../models/parameters.model";

@Injectable({
  providedIn: 'root'
})
export class LazyVehicleService extends AbstractVehicleService {

  constructor(
    private readonly http: HttpClient
  ) {
    super()
  }

  public getOutput(): Observable<number> {
    return this.http.get<number>('/api/vehicle/lazy/output').pipe(first());
  }

  public start(parameter: ParametersModel): Observable<void> {
    return this.http.post<void>('/api/vehicle/lazy/start', parameter).pipe(first());
  }

  public next(vehicle: VehicleModel): Observable<void> {
    return this.http.post<void>('/api/vehicle/lazy/next', vehicle).pipe(first());
  }

  public reset(): Observable<void> {
    return this.http.get<void>('/api/vehicle/lazy/reset').pipe(first());
  }
}
