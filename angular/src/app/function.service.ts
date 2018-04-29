import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Dataset} from './dataset/dataset.model';
import {Function} from './function/function.model';

@Injectable()
export class FunctionService {
  apiAddress: string;
  dataset: Dataset;
  functions: Function[];
  functionsUpdated = new EventEmitter();

  constructor(private _httpClient: HttpClient) {
    this.apiAddress = 'http://localhost:8080/se';
  }

  getFunctions(dataset: Dataset): void {
    this.dataset = dataset;
    console.log('getFunctions: ' + this.dataset.id);
    let functions: Array<Function> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/functions';
    const functions$ = this._httpClient.get<Array<Function>>(request);
    functions$.subscribe(value => {
      functions = value;
    }, error => {
      console.log(error);
    }, () => {
      this.functions = functions;
      this.functionsUpdated.emit(functions);
    });
  }

  getFunction(functionUri: string): Function {
    if (this.functions) {
      for (let index = 0; index < this.functions.length; index++) {
        if (this.functions[index].uri === functionUri) {
          return this.functions[index];
        }
      }
    }
    return null;
  }
}
