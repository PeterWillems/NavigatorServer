import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {RealisationModuleService} from '../realisation-module.service';
import {SeObjectType} from '../se-object-type';
import {SystemSlotModel} from '../models/system-slot.model';
import {RealisationModuleModel} from '../models/realisation-module.model';
import {SystemSlotService} from '../system-slot.service';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';
import {RealisationPortModel} from '../models/realisation-port.model';
import {RealisationPortService} from '../realisation-port.service';

@Component({
  selector: 'app-selected-realisation-module',
  templateUrl: './selected-realisation-module.component.html',
  styleUrls: ['./selected-realisation-module.component.css']
})
export class SelectedRealisationModuleComponent implements OnInit, OnChanges {
  realisationModuleType = SeObjectType.RealisationModuleModel;
  performanceType = SeObjectType.PerformanceModel;
  realisationPortType = SeObjectType.RealisationPortModel;
  isOpen = false;
  @Input() selectedRealisationModule: RealisationModuleModel;
  assembly: RealisationModuleModel;
  parts: RealisationModuleModel[];
  partsEditMode = false;
  performances: PerformanceModel[];
  performancesEditMode = false;
  ports: RealisationPortModel[];
  portsEditMode = false;
  systemSlots: SystemSlotModel[];
  systemSlotsEditMode = false;

  constructor(
    private _realisationModuleService: RealisationModuleService,
    private _performanceService: PerformanceService,
    private _systemSlotService: SystemSlotService,
    private _realisationPortService: RealisationPortService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedRealisationModuleChange = changes['selectedRealisationModule'];
    if (selectedRealisationModuleChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.performances = this.getPerformances();
    this.ports = this.getPorts();
    this.getSystemSlots();
  }

  getAssembly(): RealisationModuleModel {
    if (this.selectedRealisationModule.assembly) {
      return this._realisationModuleService.getSeObject(this.selectedRealisationModule.assembly);
    }
    return null;
  }

  getParts(): RealisationModuleModel[] {
    const parts = [];
    if (this.selectedRealisationModule.parts) {
      for (let index = 0; index < this.selectedRealisationModule.parts.length; index++) {
        parts.push(this._realisationModuleService.getSeObject(this.selectedRealisationModule.parts[index]));
      }
    }
    return parts;
  }

  getPerformances(): PerformanceModel[] {
    const performances = [];
    if (this.selectedRealisationModule.performances) {
      for (let index = 0; index < this.selectedRealisationModule.performances.length; index++) {
        performances.push(this._performanceService.getSeObject(this.selectedRealisationModule.performances[index]));
      }
    }
    return performances;
  }

  getPorts(): RealisationPortModel[] {
    const ports = [];
    if (this.selectedRealisationModule.ports) {
      for (let index = 0; index < this.selectedRealisationModule.ports.length; index++) {
        ports.push(this._realisationPortService.getSeObject(this.selectedRealisationModule.ports[index]));
      }
    }
    return ports;
  }

  getSystemSlots(): void {
    const systemSlots = [];
    this._realisationModuleService.getHamburgers(this.selectedRealisationModule.uri).subscribe(hamburgers => {
      for (let index = 0; index < hamburgers.length; index++) {
        const systemSlot = this._systemSlotService.getSeObject(hamburgers[index].functionalUnit);
        systemSlots.push(systemSlot);
      }
      this.systemSlots = systemSlots;
    });
  }

  onLabelChanged(label: string): void {
    this.selectedRealisationModule.label = label;
    this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedRealisationModule.assembly = assembly ? assembly.uri : null;
    this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
  }

  onPartsEditModeChange(editMode: boolean): void {
    console.log('onPartsEditModeChange: ' + editMode);
    this.partsEditMode = editMode;
  }

  onPartAdded(): void {
    const newPart = new RealisationModuleModel();
    newPart.label = '***';
    this.parts.push(newPart);
    console.log('Parts: ' + this.parts.toString());
  }

  onPartChanged(part: RealisationModuleModel, item: RealisationModuleModel): void {
    if (item.label === '***') {
      part.assembly = this.selectedRealisationModule.uri;
      this._realisationModuleService.updateSeObject(part);
      this.selectedRealisationModule.parts.push(part.uri);
      this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
      this.parts = this.getParts();
    } else {
      if (part === null) {
        item.assembly = null;
        this._realisationModuleService.updateSeObject(item);
        for (let index = 0; this.selectedRealisationModule.parts.length; index++) {
          if (this.selectedRealisationModule.parts[index] === item.uri) {
            this.selectedRealisationModule.parts.splice(index, 1);
            break;
          }
        }
        this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
        this.parts = this.getParts();
      }
    }
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
      this.selectedRealisationModule.performances.push(performance.uri);
      this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
      this.performances = this.getPerformances();
    } else {
      if (performance === null) {
        for (let index = 0; this.selectedRealisationModule.performances.length; index++) {
          if (this.selectedRealisationModule.performances[index] === item.uri) {
            this.selectedRealisationModule.performances.splice(index, 1);
            break;
          }
        }
        this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
        this.performances = this.getPerformances();
      }
    }
  }

  onPortsEditModeChange(editMode: boolean): void {
    console.log('onPortsEditModeChange: ' + editMode);
    this.portsEditMode = editMode;
  }

  onPortAdded(): void {
    const newPart = new RealisationPortModel();
    newPart.label = '***';
    this.ports.push(newPart);
    console.log('Ports: ' + this.ports.toString());
  }

  onPortChanged(port: RealisationPortModel, item: RealisationPortModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedRealisationModule.ports.push(port.uri);
      this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
      this.ports = this.getPorts();
    } else {
      if (port === null) {
        for (let index = 0; this.selectedRealisationModule.ports.length; index++) {
          if (this.selectedRealisationModule.ports[index] === item.uri) {
            this.selectedRealisationModule.ports.splice(index, 1);
            break;
          }
        }
        this._realisationModuleService.updateSeObject(this.selectedRealisationModule);
        this.ports = this.getPorts();
      }
    }
  }

  onSystemSlotsEditModeChange(editMode: boolean): void {
    this.systemSlotsEditMode = editMode;
  }

}
