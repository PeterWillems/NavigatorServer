import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../models/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {FunctionModel} from '../models/function.model';
import {NetworkConnectionModel} from '../models/network-connection.model';
import {NetworkConnectionService} from '../network-connection.service';
import {RealisationModuleModel} from '../models/realisation-module.model';
import {RealisationModuleService} from '../realisation-module.service';
import {RequirementService} from '../requirement.service';
import {RequirementModel} from '../models/requirement.model';

@Component({
  selector: 'app-selected-system-slot',
  templateUrl: './selected-system-slot.component.html',
  styleUrls: ['./selected-system-slot.component.css'],
})
export class SelectedSystemSlotComponent implements OnInit, OnChanges {
  systemSlotType = SeObjectType.SystemSlotModel;
  requirementType = SeObjectType.RequirementModel;
  functionType = SeObjectType.FunctionModel;
  networkConnectionType = SeObjectType.NetworkConnectionModel;
  realisationModuleType = SeObjectType.RealisationModuleModel;
  isOpen = false;
  @Input() selectedSystemSlot: SystemSlotModel;
  assembly: SystemSlotModel;
  parts: SystemSlotModel[];
  partsEditMode = false;
  requirements: RequirementModel[];
  requirementsEditMode = false;
  functions: FunctionModel[];
  functionsEditMode = false;
  interfaces: NetworkConnectionModel[];
  interfacesEditMode = false;
  realisations: RealisationModuleModel[];
  realisationsEditMode = false;

  constructor(private _systemSlotService: SystemSlotService,
              private _requirementService: RequirementService,
              private _functionService: FunctionService,
              private _networkConnectionService: NetworkConnectionService,
              private _realisationModuleService: RealisationModuleService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedSystemSlotChange = changes['selectedSystemSlot'];
    if (selectedSystemSlotChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.requirements = this.getRequirements();
    this.functions = this.getFunctions();
    this.interfaces = this.getInterfaces();
    this.getRealisations();
  }

  getAssembly(): SystemSlotModel {
    if (this.selectedSystemSlot.assembly) {
      return this._systemSlotService.getSeObject(this.selectedSystemSlot.assembly);
    }
    return null;
  }

  getParts(): SystemSlotModel[] {
    const parts = [];
    if (this.selectedSystemSlot.parts) {
      for (let index = 0; index < this.selectedSystemSlot.parts.length; index++) {
        parts.push(this._systemSlotService.getSeObject(this.selectedSystemSlot.parts[index]));
      }
    }
    return parts;
  }

  getRequirements(): RequirementModel[] {
    const requirements = [];
    if (this.selectedSystemSlot.requirements) {
      for (let index = 0; index < this.selectedSystemSlot.requirements.length; index++) {
        requirements.push(this._requirementService.getSeObject(this.selectedSystemSlot.requirements[index]));
      }
    }
    return requirements;
  }

  getFunctions(): FunctionModel[] {
    const functions = [];
    if (this.selectedSystemSlot.functions) {
      for (let index = 0; index < this.selectedSystemSlot.functions.length; index++) {
        const functionModel = this._functionService.getSeObject(this.selectedSystemSlot.functions[index]);
        functions.push(functionModel);
      }
    }
    return functions;
  }

  getInterfaces(): NetworkConnectionModel[] {
    const interfaces = [];
    if (this.selectedSystemSlot.interfaces) {
      for (let index = 0; index < this.selectedSystemSlot.interfaces.length; index++) {
        const connection = this._networkConnectionService.getSeObject(this.selectedSystemSlot.interfaces[index]);
        interfaces.push(connection);
      }
    }
    return interfaces;
  }

  getRealisations(): void {
    const realisations = [];
    this._systemSlotService.getHamburgers(this.selectedSystemSlot.uri).subscribe(hamburgers => {
      for (let index = 0; index < hamburgers.length; index++) {
        const realisation = this._realisationModuleService.getSeObject(hamburgers[index].technicalSolution);
        realisations.push(realisation);
      }
      this.realisations = realisations;
    });
  }

  onLabelChanged(label: string): void {
    this.selectedSystemSlot.label = label;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedSystemSlot.assembly = assembly ? assembly.uri : null;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
  }

  onRequirementsEditModeChange(editMode: boolean): void {
    this.requirementsEditMode = editMode;
  }

  onFunctionsEditModeChange(editMode: boolean): void {
    console.log('onFunctionsEditModeChange: ' + editMode);
    this.functionsEditMode = editMode;
  }

  onPartsEditModeChange(editMode: boolean): void {
    console.log('onPartsEditModeChange: ' + editMode);
    this.partsEditMode = editMode;
  }

  onInterfacesEditModeChange(editMode: boolean): void {
    this.interfacesEditMode = editMode;
  }

  onRealisationsEditModeChange(editMode: boolean): void {
    this.realisationsEditMode = editMode;
  }

}
