import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {HamburgerModel} from '../models/hamburger.model';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {HamburgerService} from '../hamburger.service';
import {SystemSlotService} from '../system-slot.service';
import {SystemSlotModel} from '../models/system-slot.model';
import {RealisationModuleModel} from '../models/realisation-module.model';
import {RealisationModuleService} from '../realisation-module.service';
import {PortRealisationModel} from '../models/port-realisation.model';

@Component({
  selector: 'app-selected-hamburger',
  templateUrl: './selected-hamburger.component.html',
  styleUrls: ['./selected-hamburger.component.css']
})
export class SelectedHamburgerComponent implements OnInit, OnChanges {
  @Input() selectedHamburger: HamburgerModel;
  portRealisationType = SeObjectType.PortRealisationModel;
  systemSlotType = SeObjectType.SystemSlotModel;
  realisationModuleType = SeObjectType.RealisationModuleModel;
  assembly: HamburgerModel;
  parts: HamburgerModel[];
  partsEditMode = false;
  functionalUnit: SystemSlotModel;
  technicalSolution: RealisationModuleModel;
  portRealisations: PortRealisationModel[];
  portRealisationsEditMode = false;

  constructor(private _hamburgerService: HamburgerService,
              private _systemSlotService: SystemSlotService,
              private _realisationModuleService: RealisationModuleService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedHamburgerChange = changes['selectedHamburger'];
    if (selectedHamburgerChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.functionalUnit = this.getFunctionalUnit();
    this.technicalSolution = this.getTechnicalSolution();
    this.getPortRealisations();
  }

  getAssembly(): HamburgerModel {
    if (this.selectedHamburger.assembly) {
      return this._hamburgerService.getSeObject(this.selectedHamburger.assembly);
    }
    return null;
  }

  getParts(): HamburgerModel[] {
    const parts = [];
    if (this.selectedHamburger.parts) {
      for (let index = 0; index < this.selectedHamburger.parts.length; index++) {
        parts.push(this._hamburgerService.getSeObject(this.selectedHamburger.parts[index]));
      }
    }
    return parts;
  }

  getFunctionalUnit(): SystemSlotModel {
    if (this.selectedHamburger.functionalUnit) {
      return this._systemSlotService.getSeObject(this.selectedHamburger.functionalUnit);
    }
    return null;
  }

  getTechnicalSolution(): RealisationModuleModel {
    if (this.selectedHamburger.technicalSolution) {
      return this._realisationModuleService.getSeObject(this.selectedHamburger.technicalSolution);
    }
    return null;
  }

  getPortRealisations(): void {
    if (this.selectedHamburger.portRealisations) {
      this._hamburgerService.getPortRealisations(this.selectedHamburger).subscribe(value => this.portRealisations = value);
    }
  }

  onLabelChanged(label: string): void {
    this.selectedHamburger.label = label;
    this._hamburgerService.updateSeObject(this.selectedHamburger);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedHamburger.assembly = assembly ? assembly.uri : null;
    this._hamburgerService.updateSeObject(this.selectedHamburger);
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

  onPortRealisationsEditModeChange(editMode: boolean): void {
    this.portRealisationsEditMode = editMode;
  }

}
