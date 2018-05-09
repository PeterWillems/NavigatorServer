import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {RequirementModel} from './models/requirement.model';

@Injectable()
export class RequirementService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  requirements: RequirementModel[];
  selectedRequirement: RequirementModel;

  constructor(private _httpClient: HttpClient, private _datasetService: DatasetService) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('RequirementService: new dataset: ' + this.dataset.filepath);
      this.loadRequirements(this.dataset);
    });
  }

  loadRequirements(dataset: Dataset): void {
    this.selectedRequirement = null;
    this.dataset = dataset;
    console.log('loadRequirements: ' + this.dataset.id);
    let requirements: Array<RequirementModel> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/requirements';
    const requirements$ = this._httpClient.get<Array<RequirementModel>>(request);
    requirements$.subscribe(value => {
      requirements = value;
    }, error => {
      console.log(error);
    }, () => {
      this.requirements = requirements;
      this.seObjectsUpdated.emit(requirements);
    });
  }

  createSeObject(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/requirements';
    let requirement = <RequirementModel>{uri: 'xxx', label: '', assembly: ''};
    const requirement$ = this._httpClient.post<RequirementModel>(request, requirement);
    requirement$.subscribe(value => {
      requirement = value;
    }, error => {
      console.log(error);
    }, () => {
      this.requirements.push(requirement);
      this.seObjectsUpdated.emit(this.requirements);
    });
  }

  updateSeObject(requirement: RequirementModel): void {
    const hashMark = requirement.uri.indexOf('#') + 1;
    const localName = requirement.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/requirements/' + localName;
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
