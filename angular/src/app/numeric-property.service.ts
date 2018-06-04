import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {DatasetService} from './dataset.service';
import {RequirementModel} from './models/requirement.model';
import {SeObjectType} from './se-object-type';
import {RealisationPortModel} from './models/realisation-port.model';
import {NumericPropertyModel} from './models/numeric-property.model';

@Injectable()
export class NumericPropertyService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  numericProperties: NumericPropertyModel[];
  selectedNumericProperty: NumericPropertyModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('NumericPropertyService: new dataset: ' + this.dataset.filepath);
      this.selectedNumericProperty = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedNumericProperty, dataset, SeObjectType.NumericPropertyModel).subscribe(value => {
      this.numericProperties = value;
      this.seObjectsUpdated.emit(this.numericProperties);
    });
  }

  createObject(): void {
    this.create(this.selectedNumericProperty, this.dataset, SeObjectType.NumericPropertyModel).subscribe(value => {
      this.numericProperties.push(value);
      this.seObjectsUpdated.emit(this.numericProperties);
    });
  }

  updateSeObject(numericProperty: NumericPropertyModel): void {
    const hashMark = numericProperty.uri.indexOf('#') + 1;
    const localName = numericProperty.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/numeric-properties/' + localName;
    this._httpClient.put(request, numericProperty).subscribe(value => {
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectNumericProperty(selectedNumericProperty: NumericPropertyModel) {
    this.selectedNumericProperty = selectedNumericProperty;
  }

  getSeObject(numericPropertyUri: string): NumericPropertyModel {
    if (this.numericProperties) {
      for (let index = 0; index < this.numericProperties.length; index++) {
        if (this.numericProperties[index].uri === numericPropertyUri) {
          return this.numericProperties[index];
        }
      }
    }
    return null;
  }

  getSeObjectLabel(numericPropertyUri: string): string {
    return this.getSeObject(numericPropertyUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.numericProperties;
  }
}
