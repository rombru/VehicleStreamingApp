import {Component, OnInit} from '@angular/core';
import OSM from "ol/source/OSM";
import TileLayer from "ol/layer/Tile";
import {Feature, Map, View} from "ol";
import {HttpClient} from "@angular/common/http";
import {Geometry, Point} from "ol/geom";
import {Fill, Icon, Stroke, Style} from "ol/style";
import CircleStyle from "ol/style/Circle";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import {GeoJSON} from "ol/format";
import {AppState} from "../app.state";
import {filter, flatMap, map, tap} from "rxjs/operators";
import {ParametersModel} from "../../models/parameters.model";
import {Subscription, timer} from 'rxjs';
import {RandomUtil} from "../utils/random-util";
import {VehicleModel} from 'src/models/vehicle.model';
import {VehicleTypeEnum} from "../../models/vehicle-type.enum";
import {LazyVehicleService} from 'src/services/lazy-vehicle.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {

  private map: Map;
  private route: any;
  private vectorSource: VectorSource<Geometry>;
  private vectorLayer: VectorLayer<VectorSource<Geometry>>;

  private cars: VehicleModel[] = [];
  private trucks: VehicleModel[] = [];
  private motorbikes: VehicleModel[] = [];

  private currentList: VehicleModel[] = [];
  private lastTime: number;
  private styles: any;
  private subscription: Subscription;
  private postrenderCallback;

  private routeFeature: Feature<Geometry>;
  private startMarker: Feature<Geometry>;
  private endMarker: Feature<Geometry>;

  constructor(
    readonly httpClient: HttpClient,
    readonly appState: AppState
  ) { }

  ngOnInit(): void {
    this.map = new Map({
      view: new View({
        center: [824793.75,8622784.72],
        zoom: 11,
        minZoom: 2,
        maxZoom: 19,
      }),
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      target: 'ol-map'
    });

    this.httpClient.get('assets/geojson/tunnel.json', {responseType: "json"})
      .subscribe((r : any) => this.addLayer(r.features[0].geometry));

    this.appState.parameters$
      .pipe(filter(p => p != null))
      .subscribe(params => this.startAlgorithm(params))

    this.appState.stop$
      .pipe(filter(p => p === true))
      .subscribe(_ => this.stopAlgorithm())
  }

  addLayer(polyline): void {
    this.routeFeature = new GeoJSON().readFeatures(polyline, {
      dataProjection: 'EPSG:4326',
      featureProjection: 'EPSG:3857',
    })[0];
    this.routeFeature.set('type','route');
    this.route = this.routeFeature.getGeometry();

    this.startMarker = new Feature({
      type: 'icon',
      geometry: new Point(this.route.getFirstCoordinate()),
    });
    this.endMarker = new Feature({
      type: 'icon',
      geometry: new Point(this.route.getLastCoordinate()),
    });

    this.styles = {
      'route': new Style({
        stroke: new Stroke({
          width: 6,
          color: [237, 212, 0, 0.8],
        }),
      }),
      'icon': new Style({
        image: new Icon({
          anchor: [0.5, 1],
          src: 'assets/icon/tunnel.png',
        }),
      }),
      'car': new Style({
        image: new CircleStyle({
          radius: 7,
          fill: new Fill({color: 'black'}),
          stroke: new Stroke({
            color: 'white',
            width: 2,
          }),
        }),
      }),
      'truck': new Style({
        image: new CircleStyle({
          radius: 7,
          fill: new Fill({color: 'red'}),
          stroke: new Stroke({
            color: 'white',
            width: 2,
          }),
        }),
      }),
      'motorbike': new Style({
        image: new CircleStyle({
          radius: 7,
          fill: new Fill({color: 'green'}),
          stroke: new Stroke({
            color: 'white',
            width: 2,
          }),
        }),
      }),
    };

    const styles = this.styles;
    this.vectorSource = new VectorSource({
      features: [this.routeFeature, this.startMarker, this.endMarker],
    });
    this.vectorLayer = new VectorLayer({
      source: this.vectorSource,
      style: function (feature) {
        return styles[feature.get('type')];
      },
    });

    this.map.addLayer(this.vectorLayer);
  }

  private stopAlgorithm(){
    if(this.subscription) {
      this.appState.reset().subscribe();
      this.subscription.unsubscribe();
      this.subscription = null;
      this.vectorLayer.un('postrender', this.postrenderCallback);
      this.vectorSource.clear();
      this.vectorSource.addFeatures([this.routeFeature, this.startMarker, this.endMarker]);
      this.postrenderCallback = null;
      this.map.render();

      this.currentList = [];
      this.cars = [];
      this.trucks = [];
      this.motorbikes = [];
    }
  }

  private startAlgorithm(params: ParametersModel) {
    this.lastTime = Date.now();
    const time = 60000 / params.density;
    this.appState.start(params).subscribe(_ => {
      this.subscription = timer(time, time).pipe(
        map(_ => this.createRandomVehicle(params)),
        tap(v => this.currentList.push(v)),
        flatMap(v => this.appState.next(v)),
      ).subscribe();
      this.postrenderCallback = (e) => this.moveFeatures(e, params);
      this.vectorLayer.on('postrender', this.postrenderCallback);
      this.map.render();
    });
  }

  public createRandomVehicle(params): VehicleModel {
    const totalRate = params.rateCar + params.rateMotorbike + params.rateTruck;
    const rateCar = params.rateCar/totalRate;
    const rateTruck = params.rateTruck/totalRate;

    const rand = Math.random();
    if(rand < rateCar) {
      if(this.cars.length == 0) {
        this.cars = RandomUtil.generate(1000, params.meanSpeedCar, params.sdSpeedCar)
          .map(speed => this.createVehicle(speed, VehicleTypeEnum.CAR, 'car'));

      }
      return this.cars.pop();
    } else if(rand >= rateCar && rand < (rateTruck+rateCar)) {
      if(this.trucks.length == 0) {
        this.trucks = RandomUtil.generate(1000, params.meanSpeedTruck, params.sdSpeedTruck)
          .map(speed => this.createVehicle(speed, VehicleTypeEnum.TRUCK, 'truck'));

      }
      return this.trucks.pop();
    } else {
      if(this.motorbikes.length == 0) {
        this.motorbikes = RandomUtil.generate(1000, params.meanSpeedMotorbike, params.sdSpeedMotorbike)
          .map(speed => this.createVehicle(speed, VehicleTypeEnum.MOTORBIKE, 'motorbike'));
      }
      return this.motorbikes.pop();
    }
  }

  createVehicle(speed: number, type: VehicleTypeEnum, style: string) {
    const feat = new Feature({type: style, geometry: new Point(this.route.getFirstCoordinate())});
    feat.setId(Math.random());
    return <VehicleModel>{
      speed: speed,
      type: type,
      distance: 0,
      feature: feat
    }
  }

  moveFeatures(event, params) {
    this.currentList = this.currentList.filter(c => c.distance <= 1);
    const featureLength = 25;

    for(const vehicle of this.currentList) {
      if(vehicle.distance !== 0) {
        this.vectorSource.removeFeature(vehicle.feature);
      }
      const speed = vehicle.speed * params.acceleration;
      const time = event.frameState.time;
      const elapsedTime = time - this.lastTime;
      vehicle.distance = (vehicle.distance + (((speed/60/60) * (elapsedTime/1000)) / featureLength));

      if(vehicle.distance <= 1) {
        const currentCoordinate = this.route.getCoordinateAt(vehicle.distance);
        (<Point>vehicle.feature.getGeometry()).setCoordinates(currentCoordinate);
        this.vectorSource.addFeature(vehicle.feature);
      }
    }
    this.lastTime = event.frameState.time;
    this.map.render();
  }
}
