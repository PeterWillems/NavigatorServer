import {SeObjectModel} from './se-object.model';

export class NumericPropertyModel extends SeObjectModel {
  type: string;
  datatypeValue: number;
  unit: string;

  getQueryTag(): string {
    return '/numeric-properties';
  }
}
