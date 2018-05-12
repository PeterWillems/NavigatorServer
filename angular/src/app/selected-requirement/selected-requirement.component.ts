import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {RequirementService} from '../requirement.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {RequirementModel} from '../models/requirement.model';


@Component({
  selector: 'app-selected-requirement',
  templateUrl: './selected-requirement.component.html',
  styleUrls: ['./selected-requirement.component.css'],
})
export class SelectedRequirementComponent implements OnInit, OnChanges {
  requirementType = SeObjectType.RequirementModel;
  isOpen = false;
  @Input() selectedRequirement: RequirementModel;
  assembly: RequirementModel;
  parts: RequirementModel[];
  partsEditMode = false;

  constructor(private _requirementService: RequirementService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedRequirementChange = changes['selectedPerformance'];
    if (selectedRequirementChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
  }

  getAssembly(): RequirementModel {
    if (this.selectedRequirement.assembly) {
      return this._requirementService.getSeObject(this.selectedRequirement.assembly);
    }
    return null;
  }

  getParts(): RequirementModel[] {
    const parts = [];
    if (this.selectedRequirement.parts) {
      for (let index = 0; index < this.selectedRequirement.parts.length; index++) {
        parts.push(this._requirementService.getSeObject(this.selectedRequirement.parts[index]));
      }
    }
    return parts;
  }

  onLabelChanged(label: string): void {
    this.selectedRequirement.label = label;
    this._requirementService.updateSeObject(this.selectedRequirement);
  }

  onAssemblyChanged(assembly: SeObjectModel): void {
    this.selectedRequirement.assembly = assembly ? assembly.uri : null;
    this._requirementService.updateSeObject(this.selectedRequirement);
  }

  onPartsEditModeChange(editMode: boolean): void {
    this.partsEditMode = editMode;
  }

}
