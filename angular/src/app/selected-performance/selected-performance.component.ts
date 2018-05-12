import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';

@Component({
  selector: 'app-selected-performance',
  templateUrl: './selected-performance.component.html',
  styleUrls: ['./selected-performance.component.css']
})
export class SelectedPerformanceComponent implements OnInit, OnChanges {
  performanceType = SeObjectType.PerformanceModel;
  isOpen = false;
  @Input() selectedPerformance: PerformanceModel;
  assembly: PerformanceModel;
  parts: PerformanceModel[];
  partsEditMode = false;

  constructor(private _performanceService: PerformanceService) {
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

}
