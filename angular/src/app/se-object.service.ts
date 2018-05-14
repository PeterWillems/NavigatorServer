import {EventEmitter, Injectable} from '@angular/core';
import {SeObjectModel} from './models/se-object.model';
import {Dataset} from './dataset/dataset.model';
import {SeObjectType} from './se-object-type';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

@Injectable()
export abstract class SeObjectService {
  apiAddress: string;
  seObjectsUpdated = new EventEmitter();
  protected _httpClient: HttpClient;

  constructor(_httpClient: HttpClient) {
    this.apiAddress = 'http://localhost:8080/se';
  }

  abstract loadObjects(dataset: Dataset);

  abstract getSeObjectLabel(seObjectUri: string): string ;

  abstract getSeObjects(): SeObjectModel[];

  abstract getSeObject(selectedSeObjectUri: string): SeObjectModel;

  abstract createObject(): void;

  abstract updateSeObject(seObject: SeObjectModel): void;

  protected load<T>(arg: T, dataset: Dataset, type: SeObjectType): Observable<T[]> {
    const tag = this.getHttpQueryTag(type);
    const request = this.apiAddress + '/datasets/' + dataset.id + tag;
    return this._httpClient.get<Array<T>>(request);
  }

  protected create<T>(arg: T, dataset: Dataset, type: SeObjectType): Observable<T> {
    const tag = this.getHttpQueryTag(type);
    const request = this.apiAddress + '/datasets/' + dataset.id + tag;
    return this._httpClient.post<T>(request, null);
  }

  private getHttpQueryTag(type: SeObjectType): string {
    switch (type) {
      case SeObjectType.FunctionModel:
        return '/functions';
      case SeObjectType.HamburgerModel:
        return '/hamburgers';
      case SeObjectType.SystemInterfaceModel:
        return '/system-interfaces';
      case SeObjectType.PerformanceModel:
        return '/performances';
      case SeObjectType.RealisationModuleModel:
        return '/realisation-modules';
      case SeObjectType.RealisationPortModel:
        return '/realisation-ports';
      case SeObjectType.RequirementModel:
        return '/requirements';
      case SeObjectType.SystemSlotModel:
        return '/system-slots';
      default:
        return null;
    }
  }
}
