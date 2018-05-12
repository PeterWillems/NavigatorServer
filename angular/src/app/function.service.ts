import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {FunctionModel} from './models/function.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {SystemSlotModel} from './models/system-slot.model';
import {Observable} from 'rxjs/Observable';
import {DatasetService} from './dataset.service';
import {SeObjectType} from './se-object-type';

@Injectable()
export class FunctionService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  functions: FunctionModel[];
  selectedFunction: FunctionModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('FunctionService: new dataset: ' + this.dataset.filepath);
      this.selectedFunction = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedFunction, dataset, SeObjectType.FunctionModel).subscribe(value => {
      this.functions = value;
      this.seObjectsUpdated.emit(this.functions);
    });
  }

  createObject(): void {
    this.create(this.selectedFunction, this.dataset, SeObjectType.FunctionModel).subscribe(value => {
      this.functions.push(value);
      this.seObjectsUpdated.emit(this.functions);
    });
  }

  updateSeObject(functionModel: FunctionModel): void {
    const hashMark = functionModel.uri.indexOf('#') + 1;
    const localName = functionModel.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/functions/' + localName;
    this._httpClient.put(request, functionModel).subscribe(value => {
      console.log('Assembly: ' + (<FunctionModel>value).assembly);
    }, error => {
    }, () => {
      console.log('Put operation ready');
    });
  }

  selectFunction(selectedFunction: FunctionModel) {
    this.selectedFunction = selectedFunction;
  }

  getSeObject(functionUri: string): FunctionModel {
    if (this.functions) {
      for (let index = 0; index < this.functions.length; index++) {
        if (this.functions[index].uri === functionUri) {
          return this.functions[index];
        }
      }
    }
    console.log('getSeObject5 ' + functionUri + ' / ' + this.functions);
    return null;
  }

  getSeObjectLabel(functionUri: string): string {
    return this.getSeObject(functionUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.functions;
  }
}
