import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';
import {NumericPropertyModel} from '../models/numeric-property.model';
import {NumericPropertyService} from '../numeric-property.service';

@Component({
  selector: 'app-selected-performance',
  templateUrl: './selected-performance.component.html',
  styleUrls: ['./selected-performance.component.css']
})
export class SelectedPerformanceComponent implements OnInit, OnChanges {
  performanceType = SeObjectType.PerformanceModel;
  numericPropertyType = SeObjectType.NumericPropertyModel;
  isOpen = false;
  @Input() selectedPerformance: PerformanceModel;
  assembly: PerformanceModel;
  parts: PerformanceModel[];
  partsEditMode = false;
  value: NumericPropertyModel;

  constructor(private _performanceService: PerformanceService,
              private _numericPropertyService: NumericPropertyService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedPerformanceChange = changes['selectedPerformance'];
    if (selectedPerformanceChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.value = this.getValue();
    console.log('_loadStateValues: ' + this.value);
  }

  getAssembly(): PerformanceModel {
    if (this.selectedPerformance.assembly) {
      return this._performanceService.getSeObject(this.selectedPerformance.assembly);
    }
    return null;
  }

  getParts(): PerformanceModel[] {
    const parts = [];
    if (this.selectedPerformance.parts) {
      for (let index = 0; index < this.selectedPerformance.parts.length; index++) {
        parts.push(this._performanceService.getSeObject(this.selectedPerformance.parts[index]));
      }
    }
    return parts;
  }

  getValue(): NumericPropertyModel {
    if (this.selectedPerformance.value) {
      return this._numericPropertyService.getSeObject(this.selectedPerformance.value);
    }
    return null;
  }

  onLabelChanged(label: string): void {
    this.selectedPerformance.label = label;
    this._performanceService.updateSeObject(this.selectedPerformance);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedPerformance.assembly = assembly ? assembly.uri : null;
    this._performanceService.updateSeObject(this.selectedPerformance);
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

  onValueChanged(value: NumericPropertyModel): void {
    this.selectedPerformance.value = value ? value.uri : null;
    this._performanceService.updateSeObject(this.selectedPerformance);
    this.value = this.getValue();
  }


}
