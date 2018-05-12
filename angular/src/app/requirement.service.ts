import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {RequirementModel} from './models/requirement.model';
import {SeObjectType} from './se-object-type';

@Injectable()
export class RequirementService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  requirements: RequirementModel[];
  selectedRequirement: RequirementModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('RequirementService: new dataset: ' + this.dataset.filepath);
      this.selectedRequirement = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedRequirement, dataset, SeObjectType.RequirementModel).subscribe(value => {
      this.requirements = value;
      this.seObjectsUpdated.emit(this.requirements);
    });
  }

  createObject(): void {
    this.create(this.selectedRequirement, this.dataset, SeObjectType.RequirementModel).subscribe(value => {
      this.requirements.push(value);
      this.seObjectsUpdated.emit(this.requirements);
    });
  }

  updateSeObject(requirement: RequirementModel): void {
    const hashMark = requirement.uri.indexOf('#') + 1;
    const localName = requirement.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/performances/' + localName;
    this._httpClient.put(request, requirement).subscribe(value => {
      console.log('Assembly: ' + (<RequirementModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectRequirement(selectedRequirement: RequirementModel) {
    this.selectedRequirement = selectedRequirement;
  }

  getSeObject(requirementUri: string): RequirementModel {
    if (this.requirements) {
      for (let index = 0; index < this.requirements.length; index++) {
        if (this.requirements[index].uri === requirementUri) {
          return this.requirements[index];
        }
      }
    }
    return null;
  }

  getSeObjectLabel(requirementUri: string): string {
    return this.getSeObject(requirementUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.requirements;
  }
}
