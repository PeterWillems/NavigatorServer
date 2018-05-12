import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SeObjectModel} from '../models/se-object.model';
import {RealisationModuleService} from '../realisation-module.service';
import {SeObjectType} from '../se-object-type';
import {SystemSlotModel} from '../models/system-slot.model';
import {RealisationModuleModel} from '../models/realisation-module.model';
import {SystemSlotService} from '../system-slot.service';
import {PerformanceModel} from '../models/performance.model';
import {PerformanceService} from '../performance.service';

@Component({
  selector: 'app-selected-realisation-module',
  templateUrl: './selected-realisation-module.component.html',
  styleUrls: ['./selected-realisation-module.component.css']
})
export class SelectedRealisationModuleComponent implements OnInit, OnChanges {
  realisationModuleType = SeObjectType.RealisationModuleModel;
  performanceType = SeObjectType.PerformanceModel;
  isOpen = false;
  @Input() selectedRealisationModule: RealisationModuleModel;
  assembly: RealisationModuleModel;
  parts: RealisationModuleModel[];
  partsEditMode = false;
  performances: PerformanceModel[];
  performancesEditMode = false;
  systemSlots: SystemSlotModel[];
  systemSlotsEditMode = false;

  constructor(
    private _realisationModuleService: RealisationModuleService,
    private _performanceService: PerformanceService,
    private _systemSlotService: SystemSlotService) {
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
    console.log('_loadStateValues: ' + this.performances.toString());
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
    console.log('getPerformances 1');
    if (this.selectedRealisationModule.performances) {
      console.log('getPerformances 2' + this.selectedRealisationModule.performances);
      for (let index = 0; index < this.selectedRealisationModule.performances.length; index++) {
        console.log('getPerformances 3' + this.selectedRealisationModule.performances[index]);
        performances.push(this._performanceService.getSeObject(this.selectedRealisationModule.performances[index]));
        console.log('getPerformances 4');
      }
    }
    console.log('getPerformances 5');
    return performances;
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

  onPerformancesEditModeChange(editMode: boolean): void {
    console.log('onPerformancesEditModeChange: ' + editMode);
    this.performancesEditMode = editMode;
  }

  onSystemSlotsEditModeChange(editMode: boolean): void {
    this.systemSlotsEditMode = editMode;
  }

}
