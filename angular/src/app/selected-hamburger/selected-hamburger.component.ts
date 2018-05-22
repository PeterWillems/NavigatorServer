import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {HamburgerModel} from '../models/hamburger.model';
import {SeObjectType} from '../se-object-type';
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
  hamburgerType = SeObjectType.HamburgerModel;
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

  onAssemblyChanged(assembly: HamburgerModel): void {
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedHamburger.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._hamburgerService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systemslot
    this.selectedHamburger.assembly = assembly ? assembly.uri : null;
    this._hamburgerService.updateSeObject(this.selectedHamburger);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedHamburger.uri);
      this._hamburgerService.updateSeObject(<HamburgerModel>assembly);
    }
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

  onPartAdded(): void {
    const newPart = new HamburgerModel();
    newPart.label = '***';
    this.parts.push(newPart);
    console.log('Parts: ' + this.parts.toString());
  }

  onPartChanged(part: HamburgerModel, item: HamburgerModel): void {
    if (item.label === '***') {
      part.assembly = this.selectedHamburger.uri;
      this._hamburgerService.updateSeObject(part);
      this.selectedHamburger.parts.push(part.uri);
      this._hamburgerService.updateSeObject(this.selectedHamburger);
      this.parts = this.getParts();
    } else {
      if (part === null) {
        item.assembly = null;
        this._hamburgerService.updateSeObject(item);
        for (let index = 0; this.selectedHamburger.parts.length; index++) {
          if (this.selectedHamburger.parts[index] === item.uri) {
            this.selectedHamburger.parts.splice(index, 1);
            break;
          }
        }
        this._hamburgerService.updateSeObject(this.selectedHamburger);
        this.parts = this.getParts();
      }
    }
  }

  onFunctionalUnitChanged(functionalUnit: SystemSlotModel) {
    this.selectedHamburger.functionalUnit = functionalUnit ? functionalUnit.uri : null;
    this._hamburgerService.updateSeObject(this.selectedHamburger);
    this.functionalUnit = this.getFunctionalUnit();
  }

  onTechnicalSolutionChanged(technicalSolution: RealisationModuleModel) {
    this.selectedHamburger.technicalSolution = technicalSolution ? technicalSolution.uri : null;
    this._hamburgerService.updateSeObject(this.selectedHamburger);
    this.technicalSolution = this.getTechnicalSolution();
  }

  onPortRealisationsEditModeChange(editMode: boolean): void {
    this.portRealisationsEditMode = editMode;
  }

}
