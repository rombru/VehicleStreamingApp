import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {first} from "rxjs/operators";
import {VehicleModel} from "../models/vehicle.model";
import {ParametersModel} from "../models/parameters.model";

@Injectable({
  providedIn: 'root'
})
export abstract class AbstractVehicleService {

  abstract getOutput(): Observable<number>;

  abstract start(parameter: ParametersModel): Observable<void>;

  abstract next(vehicle: VehicleModel): Observable<void>;

  abstract reset(): Observable<void>;
}
