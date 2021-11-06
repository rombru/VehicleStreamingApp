import {VehicleTypeEnum} from "./vehicle-type.enum";
import {Feature} from "ol";
import {Point} from "ol/geom";

export interface VehicleModel {
  id: number;
  speed: number;
  type: VehicleTypeEnum;
  feature: Feature<Point>;
  distance: number;
}
