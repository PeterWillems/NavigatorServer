import {EventEmitter, Injectable} from '@angular/core';
import {SeObjectModel} from './se-objectslist/se-object.model';

@Injectable()
export abstract class SeObjectService {
  seObjectsUpdated = new EventEmitter();

  constructor() {
  }

  abstract getSeObjectLabel(seObjectUri: string): string ;

  abstract getSeObjects(): SeObjectModel[];

  abstract getSeObject(selectedSeObjectUri: string): SeObjectModel;

  abstract createSeObject(): void;
}
