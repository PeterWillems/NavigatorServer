import {EventEmitter, Injectable} from '@angular/core';
import {SeObjectModel} from './se-objectslist/se-object.model';
import {Observable} from 'rxjs/Observable';

@Injectable()
export abstract class SeObjectService {
  seObjectsUpdated = new EventEmitter();

  constructor() {
  }

  abstract getSeObjectLabel(seObjectUri: string): string ;

  abstract getSeObjects(): SeObjectModel[];

  abstract getSeObject(selectedSeObjectUri: string): SeObjectModel;

  abstract createSeObject(): void;

  abstract updateSeObject(seObject: SeObjectModel): void;

  abstract getSeObjectParts(assembly: SeObjectModel): Observable<SeObjectModel[]>;
}