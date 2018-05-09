import {Component, Input, OnInit} from '@angular/core';
import {DatasetService} from '../dataset.service';
import {Dataset} from '../dataset/dataset.model';
import {FunctionService} from '../function.service';
import {FunctionModel} from '../models/function.model';
import {SeObjectModel} from '../models/se-object.model';
import {RequirementModel} from '../models/requirement.model';
import {RequirementService} from '../requirement.service';

@Component({
  selector: 'app-requirement',
  templateUrl: './requirement.component.html',
  styleUrls: ['./requirement.component.css']
})
export class RequirementComponent implements OnInit {
  selectedDataset: Dataset;
  @Input() requirementUris: string[];
  requirements: RequirementModel[];
  @Input() context: SeObjectModel;
  selectedRequirement: RequirementModel;

  constructor(private _datasetService: DatasetService, public _requirementService: RequirementService) {
    console.log('Requirement component created');
  }

  ngOnInit() {
    this._datasetService.selectedDatasetUpdated.subscribe((dataset) => {
        this.selectedDataset = dataset;
        this._requirementService.loadRequirements(this.selectedDataset);
      }
    );
    this.selectedDataset = this._datasetService.selectedDataset;

    this._requirementService.seObjectsUpdated.subscribe((requirements) => {
      this.requirements = requirements;
    });

    if (!this.context) {
      this.requirements = this._requirementService.requirements;
      this.selectedRequirement = this._requirementService.selectedRequirement;
    } else {
      if (this.requirementUris) {
        this.requirements = [];
        for (let index = 0; index < this.requirementUris.length; index++) {
          this.requirements.push(this._requirementService.getSeObject(this.requirementUris[index]));
        }
      }
    }
  }

  onSelectedRequirementChanged(seObject: SeObjectModel): void {
    this.selectedRequirement = <RequirementModel>seObject;
    this._requirementService.selectRequirement(this.selectedRequirement);
    console.log(this.selectedRequirement.uri);
  }

  createRequirement(): void {
    this._requirementService.createSeObject();
  }

  getRequirementLabel(uri: string): string {
    if (Boolean(uri)) {
      return this._requirementService.getSeObject(uri).label;
    } else {
      return '';
    }
  }


}
