import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {FunctionModel} from './function/function.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './se-objectslist/se-object.model';

@Injectable()
export class FunctionService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  functions: FunctionModel[];
  selectedFunction: FunctionModel;

  // functionsUpdated = new EventEmitter();

  constructor(private _httpClient: HttpClient) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
  }

  loadFunctions(dataset: Dataset): void {
    this.dataset = dataset;
    console.log('loadFunctions: ' + this.dataset.id);
    let functions: Array<FunctionModel> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/functions';
    const functions$ = this._httpClient.get<Array<FunctionModel>>(request);
    functions$.subscribe(value => {
      functions = value;
    }, error => {
      console.log(error);
    }, () => {
      this.functions = functions;
      this.seObjectsUpdated.emit(functions);
    });
  }

  createSeObject(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/functions';
    let functionModel = <FunctionModel>{uri: 'xxx', label: '', assembly: ''};
    const function$ = this._httpClient.post<FunctionModel>(request, functionModel);
    function$.subscribe(value => {
      functionModel = value;
    }, error => {
      console.log(error);
    }, () => {
      this.functions.push(functionModel);
      this.seObjectsUpdated.emit(this.functions);
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
    return null;
  }

  getSeObjectLabel(functionUri: string): string {
    return this.getSeObject(functionUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.functions;
  }
}
