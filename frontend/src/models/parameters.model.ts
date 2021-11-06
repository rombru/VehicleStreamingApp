import {AlgoTypeEnum} from "./algo-type.enum";

export interface ParametersModel {
  algo: AlgoTypeEnum;
  length: number;
  slopeGrade: number;
  habitualUseFactor: number;
  density: number;
  acceleration: number;
  rateCar: number;
  meanSpeedCar: number;
  sdSpeedCar: number;
  rateTruck: number;
  meanSpeedTruck: number;
  sdSpeedTruck: number;
  rateMotorbike: number;
  meanSpeedMotorbike: number;
  sdSpeedMotorbike: number;
}
