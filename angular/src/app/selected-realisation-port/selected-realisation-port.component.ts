import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {RealisationPortModel} from '../models/realisation-port.model';
import {RealisationPortService} from '../realisation-port.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';
import {SystemSlotModel} from '../models/system-slot.model';

@Component({
  selector: 'app-selected-realisation-port',
  templateUrl: './selected-realisation-port.component.html',
  styleUrls: ['./selected-realisation-port.component.css']
})
export class SelectedRealisationPortComponent implements OnInit, OnChanges {
  @Input() selectedRealisationPort: RealisationPortModel;
  assembly: RealisationPortModel;
  parts: RealisationPortModel[];
  performances: PerformanceModel[];
  realisationPortType = SeObjectType.RealisationPortModel;
  performancesEditMode = false;
  performanceType = SeObjectType.PerformanceModel;

  constructor(private _realisationPortService: RealisationPortService,
              private _performanceService: PerformanceService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedRealisationPortChange = changes['selectedRealisationPort'];
    if (selectedRealisationPortChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.performances = this.getPerformances();
  }

  onLabelChanged(label: string): void {
    this.selectedRealisationPort.label = label;
    this._realisationPortService.updateSeObject(this.selectedRealisationPort);
  }

  getAssembly(): RealisationPortModel {
    if (this.selectedRealisationPort.assembly) {
      return this._realisationPortService.getSeObject(this.selectedRealisationPort.assembly);
    }
    return null;
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedRealisationPort.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._realisationPortService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systemslot
    this.selectedRealisationPort.assembly = assembly ? assembly.uri : null;
    this._realisationPortService.updateSeObject(this.selectedRealisationPort);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedRealisationPort.uri);
      this._realisationPortService.updateSeObject(<RealisationPortModel>assembly);
    }
  }

  getParts(): RealisationPortModel[] {
    const parts = [];
    if (this.selectedRealisationPort.parts) {
      for (let index = 0; index < this.selectedRealisationPort.parts.length; index++) {
        parts.push(this._realisationPortService.getSeObject(this.selectedRealisationPort.parts[index]));
      }
    }
    return parts;
  }

  getPerformances(): PerformanceModel[] {
    const performances = [];
    if (this.selectedRealisationPort.performances) {
      for (let index = 0; index < this.selectedRealisationPort.performances.length; index++) {
        performances.push(this._performanceService.getSeObject(this.selectedRealisationPort.performances[index]));
      }
    }
    return performances;
  }

  onPerformancesEditModeChange(editMode: boolean): void {
    console.log('onPerformancesEditModeChange: ' + editMode);
    this.performancesEditMode = editMode;
  }

  onPerformanceAdded(): void {
    const newPart = new PerformanceModel();
    newPart.label = '***';
    this.performances.push(newPart);
    console.log('Performances: ' + this.performances.toString());
  }

  onPerformanceChanged(performance: PerformanceModel, item: PerformanceModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedRealisationPort.performances.push(performance.uri);
      this._realisationPortService.updateSeObject(this.selectedRealisationPort);
      this.performances = this.getPerformances();
    } else {
      if (performance === null) {
        for (let index = 0; this.selectedRealisationPort.performances.length; index++) {
          if (this.selectedRealisationPort.performances[index] === item.uri) {
            this.selectedRealisationPort.performances.splice(index, 1);
            break;
          }
        }
        this._realisationPortService.updateSeObject(this.selectedRealisationPort);
        this.performances = this.getPerformances();
      }
    }
  }
}
