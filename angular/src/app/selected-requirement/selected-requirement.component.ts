import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {RequirementService} from '../requirement.service';
import {SeObjectType} from '../se-object-type';
import {SeObjectModel} from '../models/se-object.model';
import {RequirementModel} from '../models/requirement.model';
import {NumericPropertyModel} from '../models/numeric-property.model';
import {NumericPropertyService} from '../numeric-property.service';


@Component({
  selector: 'app-selected-requirement',
  templateUrl: './selected-requirement.component.html',
  styleUrls: ['./selected-requirement.component.css'],
})
export class SelectedRequirementComponent implements OnInit, OnChanges {
  requirementType = SeObjectType.RequirementModel;
  numericPropertyType = SeObjectType.NumericPropertyModel;
  isOpen = false;
  @Input() selectedRequirement: RequirementModel;
  assembly: RequirementModel;
  parts: RequirementModel[];
  partsEditMode = false;
  minValue: NumericPropertyModel;
  maxValue: NumericPropertyModel;

  constructor(private _requirementService: RequirementService,
              private _numericPropertyService: NumericPropertyService) {
  }

  ngOnInit() {
    this._loadStateValues();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const selectedRequirementChange = changes['selectedRequirement'];
    if (selectedRequirementChange) {
      this._loadStateValues();
    }
  }

  private _loadStateValues(): void {
    this.assembly = this.getAssembly();
    this.parts = this.getParts();
    this.minValue = this.getMinValue();
    this.maxValue = this.getMaxValue();
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

  getMinValue(): NumericPropertyModel {
    if (this.selectedRequirement.minValue) {
      return this._numericPropertyService.getSeObject(this.selectedRequirement.minValue);
    }
    return null;
  }

  getMaxValue(): NumericPropertyModel {
    if (this.selectedRequirement.maxValue) {
      return this._numericPropertyService.getSeObject(this.selectedRequirement.maxValue);
    }
    return null;
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

  onMinValueChanged(minValue: NumericPropertyModel): void {
    this.selectedRequirement.minValue = minValue ? minValue.uri : null;
    this._requirementService.updateSeObject(this.selectedRequirement);
    this.minValue = this.getMinValue();
  }

  onMaxValueChanged(maxValue: NumericPropertyModel): void {
    this.selectedRequirement.maxValue = maxValue ? maxValue.uri : null;
    this._requirementService.updateSeObject(this.selectedRequirement);
    this.maxValue = this.getMaxValue();
  }

}
