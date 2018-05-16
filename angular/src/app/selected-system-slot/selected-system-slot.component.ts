import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {SystemSlotModel} from '../models/system-slot.model';
import {SystemSlotService} from '../system-slot.service';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {FunctionModel} from '../models/function.model';
import {SystemInterfaceModel} from '../models/system-interface.model';
import {SystemInterfaceService} from '../system-interface.service';
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
  systemInterfaceType = SeObjectType.SystemInterfaceModel;
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
  interfaces: SystemInterfaceModel[];
  interfacesEditMode = false;
  realisations: RealisationModuleModel[];
  realisationsEditMode = false;

  constructor(private _systemSlotService: SystemSlotService,
              private _requirementService: RequirementService,
              private _functionService: FunctionService,
              private _systemInterfaceService: SystemInterfaceService,
              private _realisationModuleService: RealisationModuleService) {
  }

  ngOnInit() {
    this._loadStateValues();
    // this._systemInterfaceService.seObjectsUpdated.subscribe(value => {
    //   this.selectedSystemInterface = this._systemInterfaceService.getSeObject(this.selectedSystemInterface.uri);
    // });
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
    console.log('getParts 1');
    const parts = [];
    console.log('getParts 2 ' + this.selectedSystemSlot.parts);
    if (this.selectedSystemSlot.parts) {
      console.log('getParts 3');
      for (let index = 0; index < this.selectedSystemSlot.parts.length; index++) {
        console.log('getParts 4 ' + this.selectedSystemSlot.parts[index]);
        parts.push(this._systemSlotService.getSeObject(this.selectedSystemSlot.parts[index]));
      }
    }
    console.log('getParts 5 ' + parts);
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

  getInterfaces(): SystemInterfaceModel[] {
    const interfaces = [];
    if (this.selectedSystemSlot.interfaces) {
      for (let index = 0; index < this.selectedSystemSlot.interfaces.length; index++) {
        const systemInterface = this._systemInterfaceService.getSeObject(this.selectedSystemSlot.interfaces[index]);
        interfaces.push(systemInterface);
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
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedSystemSlot.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._systemSlotService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systemslot
    this.selectedSystemSlot.assembly = assembly ? assembly.uri : null;
    this._systemSlotService.updateSeObject(this.selectedSystemSlot);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedSystemSlot.uri);
      this._systemSlotService.updateSeObject(<SystemSlotModel>assembly);
    }
  }

  onRequirementsEditModeChange(editMode: boolean): void {
    this.requirementsEditMode = editMode;
  }

  onRequirementAdded(): void {
    const newRequirement = new RequirementModel();
    newRequirement.label = '***';
    this.requirements.push(newRequirement);
    console.log('Requirements: ' + this.requirements.toString());
  }

  onRequirementChanged(requirement: RequirementModel, item: RequirementModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedSystemSlot.requirements.push(requirement.uri);
      this._systemSlotService.updateSeObject(this.selectedSystemSlot);
      this.requirements = this.getRequirements();
    } else {
      if (requirement === null) {
        for (let index = 0; this.selectedSystemSlot.requirements.length; index++) {
          if (this.selectedSystemSlot.requirements[index] === item.uri) {
            this.selectedSystemSlot.requirements.splice(index, 1);
            break;
          }
        }
        this._systemSlotService.updateSeObject(this.selectedSystemSlot);
        this.requirements = this.getRequirements();
      }
    }
  }

  onFunctionsEditModeChange(editMode: boolean): void {
    console.log('onFunctionsEditModeChange: ' + editMode);
    this.functionsEditMode = editMode;
  }

  onFunctionAdded(): void {
    const newFunction = new FunctionModel();
    newFunction.label = '***';
    this.functions.push(newFunction);
    console.log('Functions: ' + this.functions.toString());
  }

  onFunctionChanged(functionModel: FunctionModel, item: FunctionModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedSystemSlot.functions.push(functionModel.uri);
      this._systemSlotService.updateSeObject(this.selectedSystemSlot);
      this.functions = this.getFunctions();
    } else {
      if (functionModel === null) {
        for (let index = 0; this.selectedSystemSlot.functions.length; index++) {
          if (this.selectedSystemSlot.functions[index] === item.uri) {
            this.selectedSystemSlot.functions.splice(index, 1);
            break;
          }
        }
        this._systemSlotService.updateSeObject(this.selectedSystemSlot);
        this.functions = this.getFunctions();
      }
    }
  }

  onPartsEditModeChange(editMode: boolean): void {
    console.log('onPartsEditModeChange: ' + editMode);
    this.partsEditMode = editMode;
  }

  onPartAdded(): void {
    const newPart = new SystemSlotModel();
    newPart.label = '***';
    this.parts.push(newPart);
    console.log('Parts: ' + this.parts.toString());
  }

  onPartChanged(part: SystemSlotModel, item: SystemSlotModel): void {
    if (item.label === '***') {
      part.assembly = this.selectedSystemSlot.uri;
      this._systemSlotService.updateSeObject(part);
      this.selectedSystemSlot.parts.push(part.uri);
      this._systemSlotService.updateSeObject(this.selectedSystemSlot);
      this.parts = this.getParts();
    } else {
      if (part === null) {
        item.assembly = null;
        this._systemSlotService.updateSeObject(item);
        for (let index = 0; this.selectedSystemSlot.parts.length; index++) {
          if (this.selectedSystemSlot.parts[index] === item.uri) {
            this.selectedSystemSlot.parts.splice(index, 1);
            break;
          }
        }
        this._systemSlotService.updateSeObject(this.selectedSystemSlot);
        this.parts = this.getParts();
      }
    }
  }

  private showParts(tag: string): void {
    for (let index = 0; index < this.parts.length; index++) {
      console.log('showParts-' + tag + ' Part[' + index + ']: ' + this.parts[index].label);
    }
  }

  onInterfacesEditModeChange(editMode: boolean): void {
    this.interfacesEditMode = editMode;
  }

  onInterfaceAdded(): void {
    const newInterface = new SystemInterfaceModel();
    newInterface.label = '***';
    this.interfaces.push(newInterface);
    console.log('Interfaces: ' + this.interfaces.toString());
  }

  onInterfaceChanged(systemInterface: SystemInterfaceModel, item: SystemInterfaceModel): void {
    if (item.label === '***') {
      console.log('***!');
      this.selectedSystemSlot.interfaces.push(systemInterface.uri);
      this._systemSlotService.updateSeObject(this.selectedSystemSlot);
      this.interfaces = this.getInterfaces();
    } else {
      if (systemInterface === null) {
        for (let index = 0; this.selectedSystemSlot.interfaces.length; index++) {
          if (this.selectedSystemSlot.interfaces[index] === item.uri) {
            this.selectedSystemSlot.interfaces.splice(index, 1);
            break;
          }
        }
        this._systemSlotService.updateSeObject(this.selectedSystemSlot);
        this.interfaces = this.getInterfaces();
      }
    }
  }

  onRealisationsEditModeChange(editMode: boolean): void {
    this.realisationsEditMode = editMode;
  }

}
