import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {FunctionModel} from './function/function.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './se-objectslist/se-object.model';
import {SystemSlotModel} from './system-slot/system-slot.model';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class FunctionService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  functions: FunctionModel[];
  selectedFunction: FunctionModel;

  constructor(private _httpClient: HttpClient) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
  }

  loadFunctions(dataset: Dataset): void {
    this.selectFunction(null);
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
    return null;
  }

  getSeObjectLabel(functionUri: string): string {
    return this.getSeObject(functionUri).label;
  }

  getSeObjects(): SeObjectModel[] {
    return this.functions;
  }

  getSeObjectParts(assembly: FunctionModel): Observable<FunctionModel[]> {
    const hashMark = assembly.uri.indexOf('#') + 1;
    const localName = assembly.uri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/functions/' + localName + '/parts';
    return this._httpClient.get<Array<FunctionModel>>(request);
  }
}
