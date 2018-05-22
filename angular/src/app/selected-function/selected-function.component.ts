import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {FunctionModel} from '../models/function.model';
import {SystemInterfaceModel} from '../models/system-interface.model';
import {SystemInterfaceService} from '../system-interface.service';
import {RequirementModel} from '../models/requirement.model';
import {RequirementService} from '../requirement.service';

@Component({
  selector: 'app-selected-function',
  templateUrl: './selected-function.component.html',
  styleUrls: ['./selected-function.component.css'],
})
export class SelectedFunctionComponent implements OnInit, OnChanges {
  functionType = SeObjectType.FunctionModel;
  requirementType = SeObjectType.RequirementModel;
  systemInterfaceType = SeObjectType.SystemInterfaceModel;
  isOpen = false;
  @Input() selectedFunction: FunctionModel;
  assembly: FunctionModel;
  parts: FunctionModel[];
  partsEditMode = false;
  input: SystemInterfaceModel;
  output: SystemInterfaceModel;
  requirements: RequirementModel[];
  requirementsEditMode = false;

  constructor(private _functionService: FunctionService,
              private _requirementService: RequirementService,
              private _systemInterfaceService: SystemInterfaceService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedFunctionChange = changes['selectedFunction'];
    if (selectedFunctionChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.input = this.getInput();
    this.output = this.getOutput();
    this.requirements = this.getRequirements();
  }

  getAssembly(): FunctionModel {
    if (this.selectedFunction.assembly) {
      return this._functionService.getSeObject(this.selectedFunction.assembly);
    }
    return null;
  }

  getParts(): FunctionModel[] {
    const parts = [];
    if (this.selectedFunction.parts) {
      for (let index = 0; index < this.selectedFunction.parts.length; index++) {
        parts.push(this._functionService.getSeObject(this.selectedFunction.parts[index]));
      }
    }
    return parts;
  }

  getInput(): SystemInterfaceModel {
    if (this.selectedFunction.input) {
      return this._systemInterfaceService.getSeObject(this.selectedFunction.input);
    }
    return null;
  }

  getOutput(): SystemInterfaceModel {
    if (this.selectedFunction.output) {
      return this._systemInterfaceService.getSeObject(this.selectedFunction.output);
    }
    return null;
  }

  getRequirements(): RequirementModel[] {
    const requirements = [];
    if (this.selectedFunction.requirements) {
      for (let index = 0; index < this.selectedFunction.requirements.length; index++) {
        requirements.push(this._requirementService.getSeObject(this.selectedFunction.requirements[index]));
      }
    }
    return requirements;
  }

  onLabelChanged(label: string): void {
    this.selectedFunction.label = label;
    this._functionService.updateSeObject(this.selectedFunction);
  }

  onAssemblyChanged(assembly: FunctionModel): void {
    // update the parts of the previous assembly
    if (this.assembly) {
      for (let index = 0; index < this.assembly.parts.length; index++) {
        if (this.assembly.parts[index] === this.selectedFunction.uri) {
          this.assembly.parts.splice(index, 1);
          break;
        }
      }
      this._functionService.updateSeObject(this.assembly);
    }
    // update the assembly of the selected systemslot
    this.selectedFunction.assembly = assembly ? assembly.uri : null;
    this._functionService.updateSeObject(this.selectedFunction);
    // update the parts of the new assembly if existing
    if (assembly) {
      assembly.parts.push(this.selectedFunction.uri);
      this._functionService.updateSeObject(<FunctionModel>assembly);
    }
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

  onPartAdded(): void {
    const newPart = new FunctionModel();
    newPart.label = '***';
    this.parts.push(newPart);
    console.log('Parts: ' + this.parts.toString());
  }

  onPartChanged(part: FunctionModel, item: FunctionModel): void {
    if (item.label === '***') {
      part.assembly = this.selectedFunction.uri;
      this._functionService.updateSeObject(part);
      this.selectedFunction.parts.push(part.uri);
      this._functionService.updateSeObject(this.selectedFunction);
      this.parts = this.getParts();
    } else {
      if (part === null) {
        item.assembly = null;
        this._functionService.updateSeObject(item);
        for (let index = 0; this.selectedFunction.parts.length; index++) {
          if (this.selectedFunction.parts[index] === item.uri) {
            this.selectedFunction.parts.splice(index, 1);
            break;
          }
        }
        this._functionService.updateSeObject(this.selectedFunction);
        this.parts = this.getParts();
      }
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
      this.selectedFunction.requirements.push(requirement.uri);
      this._functionService.updateSeObject(this.selectedFunction);
      this.requirements = this.getRequirements();
    } else {
      if (requirement === null) {
        for (let index = 0; this.selectedFunction.requirements.length; index++) {
          if (this.selectedFunction.requirements[index] === item.uri) {
            this.selectedFunction.requirements.splice(index, 1);
            break;
          }
        }
        this._functionService.updateSeObject(this.selectedFunction);
        this.requirements = this.getRequirements();
      }
    }
  }


}
