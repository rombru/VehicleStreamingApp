import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {ParametersModel} from "../../models/parameters.model";
import {AppState} from "../app.state";
import {AlgoTypeEnum} from "../../models/algo-type.enum";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss']
})
export class FormComponent implements OnInit {

  public readonly AlgoTypeEnum = AlgoTypeEnum;
  public readonly Color = "primary";

  public form: FormGroup;
  public result: number;

  constructor(
    private readonly fb: FormBuilder,
    private readonly appState: AppState,
  ) {
    this.form = this.fb.group({
      algo: [AlgoTypeEnum.STREAMQRE],
      density: [100],
      acceleration: [1],
      slopeGrade: [2],
      length: [4],
      habitualUseFactor: [0.90],
      rateCar: [0.7],
      meanSpeedCar: [110],
      sdSpeedCar: [10],
      rateTruck: [0.2],
      meanSpeedTruck: [90],
      sdSpeedTruck: [7],
      rateMotorbike: [0.1],
      meanSpeedMotorbike: [110],
      sdSpeedMotorbike: [10],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    const value : ParametersModel = this.form.getRawValue();
    this.appState.setServiceType(value.algo);
    this.appState.setParameters(value);
  }

  onReset(): void {
    this.form.enable();
    this.appState.setStop(true);
  }

  onGetResult(): void {
    this.appState.getOutput().subscribe(o => this.result = o);
  }

}
