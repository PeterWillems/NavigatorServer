import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {FunctionService} from '../function.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {FunctionModel} from '../models/function.model';
import {NetworkConnectionModel} from '../models/network-connection.model';
import {NetworkConnectionService} from '../network-connection.service';
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
  networkConnectionType = SeObjectType.NetworkConnectionModel;
  isOpen = false;
  @Input() selectedFunction: FunctionModel;
  assembly: FunctionModel;
  parts: FunctionModel[];
  partsEditMode = false;
  input: NetworkConnectionModel;
  output: NetworkConnectionModel;
  requirements: RequirementModel[];
  requirementsEditMode = false;

  constructor(private _functionService: FunctionService,
              private _requirementService: RequirementService,
              private _networkConnectionService: NetworkConnectionService) {
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

  getInput(): NetworkConnectionModel {
    if (this.selectedFunction.input) {
      return this._networkConnectionService.getSeObject(this.selectedFunction.input);
    }
    return null;
  }

  getOutput(): NetworkConnectionModel {
    if (this.selectedFunction.output) {
      return this._networkConnectionService.getSeObject(this.selectedFunction.output);
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

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedFunction.assembly = assembly ? assembly.uri : null;
    this._functionService.updateSeObject(this.selectedFunction);
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

  onRequirementsEditModeChange(editMode: boolean): void {
    this.requirementsEditMode = editMode;
  }

}
